package shipsensor.simulator.task;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.SensorDict;
import shipsensor.entity.ShipInfo;
import shipsensor.simulator.cache.SensorConfigCache;
import shipsensor.simulator.cache.SensorDictCache;
import shipsensor.simulator.cache.ShipCache;
import shipsensor.simulator.generator.GeneratorManager;
import shipsensor.simulator.generator.SensorGenerator;
import shipsensor.simulator.model.SensorDataMessage;
import shipsensor.simulator.queue.SensorDataQueue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 低频实时传感器数据生成任务。
 *
 * 设计目标：持续产生"实时"数据，同时严格控制写入速度，
 * 避免长期部署时撑满数据库。
 *
 * 限速策略：每个周期只随机抽取少量传感器配置（默认 10 个），
 * 每个配置生成 1 条数据。默认周期 10 秒，即约 1 条/秒 ≈ 8.6 万条/天。
 * 可通过配置调慢：
 *   simulator.realtime.interval-seconds  周期秒数（默认 10）
 *   simulator.realtime.per-tick          每周期抽样条数（默认 10）
 *   simulator.realtime.enabled           开关（默认 true）
 *
 * 该任务使用独立守护线程运行，不依赖 Spring 调度线程池，
 * 因此不会启用其它高频模拟器，也不受全局调度开关影响。
 * 生成值复用各类型 SensorGenerator，数据经现有队列批量落库。
 */
@Component
public class RealtimeLowFrequencyTask {

    @Value("${simulator.realtime.enabled:true}")
    private boolean enabled;

    @Value("${simulator.realtime.interval-seconds:10}")
    private long intervalSeconds;

    @Value("${simulator.realtime.per-tick:10}")
    private int perTick;

    private final SensorConfigCache sensorConfigCache;
    private final SensorDictCache sensorDictCache;
    private final ShipCache shipCache;
    private final GeneratorManager generatorManager;
    private final SensorDataQueue sensorDataQueue;

    private volatile boolean running = true;
    private Thread worker;

    public RealtimeLowFrequencyTask(
            SensorConfigCache sensorConfigCache,
            SensorDictCache sensorDictCache,
            ShipCache shipCache,
            GeneratorManager generatorManager,
            SensorDataQueue sensorDataQueue
    ) {
        this.sensorConfigCache = sensorConfigCache;
        this.sensorDictCache = sensorDictCache;
        this.shipCache = shipCache;
        this.generatorManager = generatorManager;
        this.sensorDataQueue = sensorDataQueue;
    }

    @PostConstruct
    public void start() {
        worker = new Thread(this::loop, "realtime-low-freq");
        worker.setDaemon(true);
        worker.start();
        System.out.println("[realtime-low-freq] started, interval=" + intervalSeconds
                + "s, perTick=" + perTick);
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (worker != null) {
            worker.interrupt();
        }
    }

    private void loop() {
        while (running) {
            try {
                Thread.sleep(Math.max(1, intervalSeconds) * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (!enabled) {
                continue;
            }
            try {
                tick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        List<SensorConfig> configs = sensorConfigCache.getAll();
        if (configs.isEmpty()) {
            return;
        }

        // 随机抽取少量配置，控制写入量
        List<SensorConfig> sampled = new ArrayList<>(configs);
        Collections.shuffle(sampled, ThreadLocalRandom.current());
        int n = Math.min(Math.max(1, perTick), sampled.size());

        LocalDateTime now = LocalDateTime.now();
        int produced = 0;

        for (int i = 0; i < n; i++) {
            SensorConfig config = sampled.get(i);
            if (config.getStatus() != null && config.getStatus() != 1) {
                continue; // 只为启用中的传感器生成数据
            }

            ShipInfo ship = shipCache.get(config.getShipId());
            if (ship == null) {
                continue;
            }

            SensorDict dict = sensorDictCache.get(config.getTypeId());
            if (dict == null) {
                continue;
            }

            SensorGenerator generator = generatorManager.getGenerator(dict.getTypeCode());
            if (generator == null) {
                continue;
            }

            Double value;
            try {
                value = generator.generate(ship, config);
            } catch (Exception e) {
                continue;
            }
            if (value == null) {
                continue;
            }

            SensorDataMessage message = new SensorDataMessage();
            message.setConfigId(config.getId());
            message.setShipId(ship.getId());
            message.setValue(value);
            message.setRecordedAt(now);
            sensorDataQueue.offer(message);
            produced++;
        }

        System.out.println("[realtime-low-freq] produced " + produced + " sensor data points");
    }
}

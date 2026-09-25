package shipsensor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.SensorData;
import shipsensor.simulator.factory.SensorDataFactory;
import shipsensor.simulator.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 传感器历史数据回填服务
 *
 * <p>用 SensorDataFactory（原空壳死类）为指定船舶的启用传感器批量生成近 N 分钟
 * 的历史读数，供前端"传感器回放 / 分析"有真实曲线可看。默认 dryRun 只预览不落库。</p>
 */
@Service
@RequiredArgsConstructor
public class SensorBackfillService {

    private final SensorConfigService sensorConfigService;
    private final SensorDataService sensorDataService;

    /**
     * @param shipId  目标船舶
     * @param count   每个传感器生成的条数（1..500 限幅）
     * @param minutes 回溯时间窗（分钟），均匀分布
     * @param dryRun  true=仅返回预览样本；false=写入 sensor_data
     */
    public Map<String, Object> backfill(Integer shipId, int count, int minutes, boolean dryRun) {

        int safeCount = Math.max(1, Math.min(count, 500));
        int safeMinutes = Math.max(5, Math.min(minutes, 24 * 60));

        LambdaQueryWrapper<SensorConfig> wrapper = new LambdaQueryWrapper<>();
        if (shipId != null) {
            wrapper.eq(SensorConfig::getShipId, shipId);
        }
        wrapper.eq(SensorConfig::getStatus, 1);

        List<SensorConfig> configs = sensorConfigService.list(wrapper);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("shipId", shipId);
        result.put("configCount", configs.size());
        result.put("perConfig", safeCount);
        result.put("windowMinutes", safeMinutes);
        result.put("dryRun", dryRun);

        if (configs.isEmpty()) {
            result.put("generated", 0);
            result.put("preview", new ArrayList<>());
            result.put("message", "该船没有启用的传感器配置");
            return result;
        }

        SensorDataFactory factory = new SensorDataFactory();
        LocalDateTime end = TimeUtil.now();
        long stepMs = (safeMinutes * 60_000L) / safeCount;

        List<SensorData> batch = new ArrayList<>();
        for (SensorConfig cfg : configs) {
            for (int i = 0; i < safeCount; i++) {
                LocalDateTime t = end.minusNanos((safeCount - 1 - i) * stepMs * 1_000_000L);
                batch.add(factory.create(cfg, t));
            }
        }

        List<Map<String, Object>> preview = new ArrayList<>();
        for (int i = 0; i < Math.min(8, batch.size()); i++) {
            SensorData d = batch.get(i);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("configId", d.getConfigId());
            row.put("shipId", d.getShipId());
            row.put("dataValue", d.getDataValue());
            row.put("recordedAt", TimeUtil.format(d.getRecordedAt()));
            preview.add(row);
        }

        result.put("generated", dryRun ? 0 : batch.size());
        result.put("wouldGenerate", batch.size());
        result.put("preview", preview);

        if (!dryRun) {
            sensorDataService.saveBatch(batch);
        }
        return result;
    }
}

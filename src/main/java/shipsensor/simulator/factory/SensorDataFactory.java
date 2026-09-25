package shipsensor.simulator.factory;

import shipsensor.entity.SensorConfig;
import shipsensor.entity.SensorData;
import shipsensor.simulator.util.RandomUtil;
import shipsensor.simulator.util.SensorMath;
import shipsensor.simulator.util.TimeUtil;

import java.time.LocalDateTime;

/**
 * 传感器数据工厂
 *
 * <p>原为空壳死类。现补实为：基于某条 SensorConfig 产出一条带物理漂移的
 * SensorData 记录，供历史数据回填（SensorBackfillService）与模拟器复用。</p>
 */
public class SensorDataFactory {

    /**
     * 按传感器类型语义给出基线值区间（无字典时的保守默认）
     */
    private static double baselineOf(SensorConfig config) {
        String name = config.getSensorName() == null ? "" : config.getSensorName().toLowerCase();
        if (name.contains("rpm")) return RandomUtil.randomDouble(600, 1800);
        if (name.contains("temp") || name.contains("温度")) return RandomUtil.randomDouble(40, 95);
        if (name.contains("fuel") || name.contains("油")) return RandomUtil.randomDouble(20, 90);
        if (name.contains("pressure") || name.contains("压")) return RandomUtil.randomDouble(2.0, 6.0);
        if (name.contains("volt") || name.contains("电")) return RandomUtil.randomDouble(22, 27);
        return RandomUtil.randomDouble(0, 100);
    }

    /**
     * 生成一条读数：基线 + 随机扰动 + GPS 式漂移噪声
     */
    public SensorData create(SensorConfig config, LocalDateTime recordedAt) {

        double base = baselineOf(config);
        double noisy = SensorMath.addNoise(base, base * 0.03);

        SensorData data = new SensorData();
        data.setConfigId(config.getId() == null ? null : config.getId().longValue());
        data.setShipId(config.getShipId());
        data.setDataValue(Math.round(noisy * 1000) / 1000.0);
        data.setRecordedAt(recordedAt);
        data.setCreateTime(TimeUtil.now());
        return data;
    }

    /**
     * 便捷方法：以当前时间为记录时刻
     */
    public SensorData create(SensorConfig config) {
        return create(config, TimeUtil.now());
    }
}

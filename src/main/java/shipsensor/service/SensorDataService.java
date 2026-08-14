package shipsensor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import shipsensor.entity.SensorData;

public interface SensorDataService extends IService<SensorData> {
    // 针对亿级数据，以后可能会在这里写特殊的批量插入或聚合查询方法
}
package shipsensor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shipsensor.entity.AlertRecord;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.ShipInfo;
import shipsensor.entity.WeatherRegion;
import shipsensor.inter.AlertRecordMapper;
import shipsensor.inter.SensorConfigMapper;
import shipsensor.inter.ShipInfoMapper;
import shipsensor.inter.WeatherRegionMapper;
import shipsensor.service.AlertRecordService;
import shipsensor.service.SensorConfigService;
import shipsensor.service.ShipInfoService;
import shipsensor.service.WeatherRegionService;
import shipsensor.service.DashboardService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ShipInfoService shipInfoService;

    @Autowired
    private SensorConfigService sensorConfigService;

    @Autowired
    private AlertRecordService alertRecordService;

    @Autowired
    private WeatherRegionService weatherRegionService;

    @Autowired
    private ShipInfoMapper shipInfoMapper;

    @Autowired
    private AlertRecordMapper alertRecordMapper;

    @Override
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("shipCount", shipInfoService.count());
        stats.put("sensorConfigCount", sensorConfigService.count());
        stats.put("alertCount", alertRecordService.count());
        stats.put("weatherCount", weatherRegionService.count());
        return stats;
    }

    @Override
    public List<Map<String, Object>> getAlertStatusSummary() {
        return alertRecordMapper.selectAlertStatusSummary();
    }

    @Override
    public List<Map<String, Object>> getShipTypeDistribution() {
        return shipInfoMapper.selectShipTypeDistribution();
    }

    @Override
    public List<Map<String, Object>> getAlertHourDistribution() {
        return alertRecordMapper.selectAlertHourDistribution();
    }
}

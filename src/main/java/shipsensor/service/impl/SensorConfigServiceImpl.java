package shipsensor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import shipsensor.entity.SensorConfig;
import shipsensor.inter.SensorConfigMapper;
import shipsensor.service.SensorConfigService;

@Service
public class SensorConfigServiceImpl extends ServiceImpl<SensorConfigMapper, SensorConfig> implements SensorConfigService {
}
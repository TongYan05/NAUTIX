package shipsensor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import shipsensor.entity.SensorDict;
import shipsensor.inter.SensorDictMapper;
import shipsensor.service.SensorDictService;

@Service
public class SensorDictServiceImpl extends ServiceImpl<SensorDictMapper, SensorDict> implements SensorDictService {
}
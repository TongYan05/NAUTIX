package shipsensor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import shipsensor.entity.AlertEvent;
import shipsensor.inter.AlertEventMapper;
import shipsensor.service.AlertEventService;

@Service
public class AlertEventServiceImpl
        extends ServiceImpl<
        AlertEventMapper,
        AlertEvent>
        implements AlertEventService {

}
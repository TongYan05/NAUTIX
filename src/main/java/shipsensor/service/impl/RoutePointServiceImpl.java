package shipsensor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import shipsensor.entity.RoutePoint;
import shipsensor.inter.RoutePointMapper;
import shipsensor.service.RoutePointService;
import shipsensor.inter.RoutePointMapper;

@Service
public class RoutePointServiceImpl
        extends ServiceImpl<
        RoutePointMapper,
        RoutePoint>
        implements RoutePointService {

}
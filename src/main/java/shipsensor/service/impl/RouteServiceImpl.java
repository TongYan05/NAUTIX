package shipsensor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import shipsensor.entity.Route;
import shipsensor.inter.RouteMapper;
import shipsensor.service.RouteService;

@Service
public class RouteServiceImpl
        extends ServiceImpl<
        RouteMapper,
        Route>
        implements RouteService {

}
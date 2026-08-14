package shipsensor.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import shipsensor.entity.Port;
import shipsensor.inter.PortMapper;
import shipsensor.service.PortService;


@Service
public class PortServiceImpl
        extends ServiceImpl<PortMapper, Port>
        implements PortService {


}
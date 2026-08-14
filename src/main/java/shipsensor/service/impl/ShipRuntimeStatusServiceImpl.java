package shipsensor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import shipsensor.entity.ShipRuntimeStatus;
import shipsensor.inter.ShipRuntimeStatusMapper;
import shipsensor.service.ShipRuntimeStatusService;

@Service
public class ShipRuntimeStatusServiceImpl
        extends ServiceImpl<
        ShipRuntimeStatusMapper,
        ShipRuntimeStatus>
        implements ShipRuntimeStatusService {

}
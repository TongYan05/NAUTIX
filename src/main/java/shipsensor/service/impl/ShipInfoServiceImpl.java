package shipsensor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import shipsensor.entity.ShipInfo;
import shipsensor.inter.ShipInfoMapper; // 注意引用你刚才创建的 Mapper
import shipsensor.service.ShipInfoService;

/**
 * 船舶基础信息 Service 实现类
 */
@Service
public class ShipInfoServiceImpl extends ServiceImpl<ShipInfoMapper, ShipInfo> implements ShipInfoService {
    // 具体的业务逻辑写在这里
    // 如果需要调用 Mapper，可以直接使用 this.baseMapper
}
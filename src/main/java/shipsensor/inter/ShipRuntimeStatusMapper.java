package shipsensor.inter;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import shipsensor.entity.ShipRuntimeStatus;


@Mapper
public interface ShipRuntimeStatusMapper
        extends BaseMapper<ShipRuntimeStatus> {


    ShipRuntimeStatus selectByShipId(
            Integer shipId
    );


    int update(
            ShipRuntimeStatus status
    );


    int insert(
            ShipRuntimeStatus status
    );

}
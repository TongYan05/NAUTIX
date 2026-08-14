package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import shipsensor.entity.ShipInfo;

import java.util.List;
import java.util.Map;

@Mapper
public interface ShipInfoMapper extends BaseMapper<ShipInfo> {

    @Select("SELECT ship_type AS shipType, COUNT(*) AS count FROM ship_info GROUP BY ship_type")
    List<Map<String, Object>> selectShipTypeDistribution();
}

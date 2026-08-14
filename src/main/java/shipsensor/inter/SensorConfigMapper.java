package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.SensorData;

import java.util.List;

@Mapper
public interface SensorConfigMapper extends BaseMapper<SensorConfig> {

    @Select("""
            SELECT *
            FROM sensor_config
            WHERE ship_id = #{shipId}
            """)
    List<SensorConfig> selectByShipId(Integer shipId);

    List<SensorConfig> selectPageList(
            @Param("offset") Integer offset,
            @Param("size") Integer size
    );
}

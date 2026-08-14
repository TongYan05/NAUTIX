package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import shipsensor.entity.SensorData;

import java.util.List;
import java.util.Map;


@Mapper
public interface SensorDataMapper
        extends BaseMapper<SensorData> {


    void insertBatch(
            @Param("list") List<SensorData> list
    );

    @Select("SELECT id, ship_id, config_id, data_value, recorded_at FROM sensor_data WHERE ship_id = #{shipId} ORDER BY id DESC LIMIT #{limit}")
    List<SensorData> selectLatestTrendByShip(@Param("shipId") Integer shipId, @Param("limit") int limit);

    @Select("SELECT id, ship_id, config_id, data_value, recorded_at FROM sensor_data ORDER BY id DESC LIMIT #{limit}")
    List<SensorData> selectLatestTrend(@Param("limit") int limit);

    /**
     * 数据库级波动性统计：仅扫描最近 scan 行（主键范围），
     * 按 config_id 分组返回条数、标准差、均值，避免前端拉原始数据做聚合。
     */
    @Select("SELECT config_id AS configId, COUNT(*) AS cnt, " +
            "STDDEV_POP(data_value) AS stdDev, AVG(data_value) AS mean " +
            "FROM sensor_data " +
            "WHERE id >= (SELECT COALESCE(MAX(id), 0) - #{scan} FROM sensor_data) " +
            "GROUP BY config_id " +
            "HAVING COUNT(*) >= 2")
    List<Map<String, Object>> selectVolatilityStats(@Param("scan") long scan);

}

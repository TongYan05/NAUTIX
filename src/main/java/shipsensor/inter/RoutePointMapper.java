package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import shipsensor.entity.RoutePoint;
import java.util.List;

@Mapper
public interface RoutePointMapper extends BaseMapper<RoutePoint> {
    void insertBatch(List<RoutePoint> points);
}

package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import shipsensor.entity.Route;
import java.util.List;

@Mapper
public interface RouteMapper extends BaseMapper<Route> {
    Long selectCount(Object o);
    void insertBatch(List<Route> routes);
}

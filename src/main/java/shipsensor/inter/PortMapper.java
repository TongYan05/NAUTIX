package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import shipsensor.entity.Port;

import java.util.List;

@Mapper
public interface PortMapper extends BaseMapper<Port> {
    void insertBatch(List<Port> ports);
}

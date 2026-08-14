package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import shipsensor.entity.AlertEvent;

@Mapper
public interface AlertEventMapper
        extends BaseMapper<AlertEvent> {

}
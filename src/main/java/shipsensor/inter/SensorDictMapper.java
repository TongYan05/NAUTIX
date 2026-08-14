package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import shipsensor.entity.SensorDict;

/**
 * 传感器类型字典表 Mapper 接口
 */
@Mapper
public interface SensorDictMapper extends BaseMapper<SensorDict> {
}
package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import shipsensor.entity.WeatherRegion;

@Mapper
public interface WeatherRegionMapper
        extends BaseMapper<WeatherRegion> {

}
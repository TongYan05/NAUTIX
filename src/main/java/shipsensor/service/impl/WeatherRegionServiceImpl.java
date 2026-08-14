package shipsensor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import shipsensor.entity.WeatherRegion;
import shipsensor.inter.WeatherRegionMapper;
import shipsensor.service.WeatherRegionService;

@Service
public class WeatherRegionServiceImpl
        extends ServiceImpl<
        WeatherRegionMapper,
        WeatherRegion>
        implements WeatherRegionService {

}
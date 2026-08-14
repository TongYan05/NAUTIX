package shipsensor.simulator.cache;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.WeatherRegion;
import shipsensor.inter.WeatherRegionMapper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class WeatherCache {

    private final WeatherRegionMapper weatherRegionMapper;

    @Getter
    private final List<WeatherRegion> weatherList =
            new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        refresh();
    }

    public void refresh() {
        weatherList.clear();
        weatherList.addAll(weatherRegionMapper.selectList(null));
    }

    /**
     * 随机天气
     */
    public WeatherRegion randomWeather() {

        if (weatherList.isEmpty()) {
            return null;
        }

        int index = (int) (Math.random() * weatherList.size());

        return weatherList.get(index);

    }

    /**
     * 根据ID获取天气
     */
    public WeatherRegion get(Long id) {

        if (id == null) {
            return null;
        }

        for (WeatherRegion weather : weatherList) {

            if (id.equals(weather.getId())) {
                return weather;
            }

        }

        return null;
    }

}
package shipsensor.controller.system;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.WeatherRegion;
import shipsensor.service.WeatherRegionService;

import java.util.List;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherRegionController {

    private final WeatherRegionService weatherService;

    @GetMapping
    public List<WeatherRegion> list() {
        return weatherService.list();
    }

    @GetMapping("/{id}")
    public WeatherRegion get(@PathVariable Long id) {
        return weatherService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody WeatherRegion weather) {
        return weatherService.save(weather);
    }

    @PutMapping
    public boolean update(@RequestBody WeatherRegion weather) {
        return weatherService.updateById(weather);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return weatherService.removeById(id);
    }

}
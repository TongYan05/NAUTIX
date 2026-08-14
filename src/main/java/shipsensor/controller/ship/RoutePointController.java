package shipsensor.controller.ship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.RoutePoint;
import shipsensor.service.RoutePointService;

import java.util.List;

@RestController
@RequestMapping("/route-point")
@RequiredArgsConstructor
public class RoutePointController {

    private final RoutePointService routePointService;

    @GetMapping
    public List<RoutePoint> list() {
        return routePointService.list();
    }

    @GetMapping("/{id}")
    public RoutePoint get(@PathVariable Long id) {
        return routePointService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody RoutePoint point) {
        return routePointService.save(point);
    }

    @PutMapping
    public boolean update(@RequestBody RoutePoint point) {
        return routePointService.updateById(point);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return routePointService.removeById(id);
    }

}
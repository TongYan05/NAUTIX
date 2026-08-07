package shipsensor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.Route;
import shipsensor.service.RouteService;

import java.util.List;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public List<Route> list() {
        return routeService.list();
    }

    @GetMapping("/{id}")
    public Route get(@PathVariable Long id) {
        return routeService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody Route route) {
        return routeService.save(route);
    }

    @PutMapping
    public boolean update(@RequestBody Route route) {
        return routeService.updateById(route);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return routeService.removeById(id);
    }

}
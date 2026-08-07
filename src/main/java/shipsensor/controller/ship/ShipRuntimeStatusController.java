package shipsensor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.ShipRuntimeStatus;
import shipsensor.service.ShipRuntimeStatusService;

import java.util.List;

@RestController
@RequestMapping("/runtime")
@RequiredArgsConstructor
public class ShipRuntimeStatusController {

    private final ShipRuntimeStatusService runtimeService;

    @GetMapping
    public List<ShipRuntimeStatus> list() {
        return runtimeService.list();
    }

    @GetMapping("/{shipId}")
    public ShipRuntimeStatus get(@PathVariable Integer shipId) {
        return runtimeService.getById(shipId);
    }

    @PostMapping
    public boolean save(@RequestBody ShipRuntimeStatus runtime) {
        return runtimeService.save(runtime);
    }

    @PutMapping
    public boolean update(@RequestBody ShipRuntimeStatus runtime) {
        return runtimeService.updateById(runtime);
    }

    @DeleteMapping("/{shipId}")
    public boolean delete(@PathVariable Integer shipId) {
        return runtimeService.removeById(shipId);
    }

}
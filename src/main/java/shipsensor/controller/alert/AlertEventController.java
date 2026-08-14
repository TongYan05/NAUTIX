package shipsensor.controller.alert;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.AlertEvent;
import shipsensor.service.AlertEventService;

import java.util.List;

@RestController
@RequestMapping("/alert-event")
@RequiredArgsConstructor
public class AlertEventController {

    private final AlertEventService alertEventService;

    @GetMapping
    public List<AlertEvent> list() {
        return alertEventService.list();
    }

    @GetMapping("/{id}")
    public AlertEvent get(@PathVariable Long id) {
        return alertEventService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody AlertEvent event) {
        return alertEventService.save(event);
    }

    @PutMapping
    public boolean update(@RequestBody AlertEvent event) {
        return alertEventService.updateById(event);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return alertEventService.removeById(id);
    }

}
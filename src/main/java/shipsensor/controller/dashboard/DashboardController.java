package shipsensor.controller.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shipsensor.service.DashboardService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return dashboardService.getStats();
    }

    @GetMapping("/alert-summary")
    public List<Map<String, Object>> getAlertSummary() {
        return dashboardService.getAlertStatusSummary();
    }

    @GetMapping("/ship-type-dist")
    public List<Map<String, Object>> getShipTypeDist() {
        return dashboardService.getShipTypeDistribution();
    }

    @GetMapping("/alert-hour-dist")
    public List<Map<String, Object>> getAlertHourDist() {
        return dashboardService.getAlertHourDistribution();
    }
}

package shipsensor.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    Map<String, Long> getStats();

    List<Map<String, Object>> getAlertStatusSummary();

    List<Map<String, Object>> getShipTypeDistribution();

    List<Map<String, Object>> getAlertHourDistribution();
}

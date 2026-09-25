package shipsensor.controller.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shipsensor.service.SensorBackfillService;

import java.util.Map;

/**
 * 传感器数据管理接口（回填/运维向）
 */
@RestController
@RequestMapping("/admin/sensor")
@RequiredArgsConstructor
public class SensorAdminController {

    private final SensorBackfillService sensorBackfillService;

    /**
     * 为指定船舶的启用传感器生成近 minutes 分钟的历史读数。
     * 默认 dryRun=true 只预览不写库；count 每传感器条数（1..500）。
     */
    @PostMapping("/backfill")
    public Map<String, Object> backfill(@RequestParam(required = false) Integer shipId,
                                         @RequestParam(defaultValue = "50") int count,
                                         @RequestParam(defaultValue = "60") int minutes,
                                         @RequestParam(defaultValue = "true") boolean dryRun) {
        return sensorBackfillService.backfill(shipId, count, minutes, dryRun);
    }
}

package shipsensor.controller.geo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shipsensor.service.geo.GeoMetricsService;

import java.util.Map;

/**
 * 地理计算接口
 *
 * <p>把模拟器死工具簇（GeoUtil / GpsCalculator / Coordinate / SensorMath /
 * RandomUtil / TimeUtil）以 REST 形式盘活：测距、航线里程审计、船位推算。</p>
 */
@RestController
@RequestMapping("/geo")
@RequiredArgsConstructor
public class GeoController {

    private final GeoMetricsService geoMetricsService;

    /**
     * 两点大圆距离（海里）
     */
    @GetMapping("/haversine")
    public Map<String, Object> haversine(@RequestParam double lat1,
                                         @RequestParam double lon1,
                                         @RequestParam double lat2,
                                         @RequestParam double lon2) {
        double nm = geoMetricsService.haversineNm(lat1, lon1, lat2, lon2);
        return Map.of(
                "lat1", lat1, "lon1", lon1,
                "lat2", lat2, "lon2", lon2,
                "distanceNm", Math.round(nm * 1000) / 1000.0,
                "distanceKm", Math.round(nm * 1.852 * 1000) / 1000.0);
    }

    /**
     * 实测某条航线的逐段里程，并与申报里程对比
     */
    @GetMapping("/route/{routeId}/measure")
    public Map<String, Object> measureRoute(@PathVariable Long routeId) {
        return geoMetricsService.measureRoute(routeId);
    }

    /**
     * 船位推算：当前位置 + 航向 + 航速（节）+ 分钟 → 未来坐标
     */
    @GetMapping("/positions/advance")
    public Map<String, Object> advance(@RequestParam double latitude,
                                       @RequestParam double longitude,
                                       @RequestParam(defaultValue = "0") double heading,
                                       @RequestParam double speedKnots,
                                       @RequestParam(defaultValue = "60") double minutes) {
        return geoMetricsService.advancePosition(latitude, longitude, heading, speedKnots, minutes);
    }
}

package shipsensor.service.geo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shipsensor.entity.Route;
import shipsensor.entity.RoutePoint;
import shipsensor.service.RoutePointService;
import shipsensor.service.RouteService;
import shipsensor.simulator.model.Coordinate;
import shipsensor.simulator.util.GeoUtil;
import shipsensor.simulator.util.GpsCalculator;
import shipsensor.simulator.util.RandomUtil;
import shipsensor.simulator.util.SensorMath;
import shipsensor.simulator.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 地理计算服务
 *
 * <p>把原本零引用的模拟器工具簇（GeoUtil / GpsCalculator / Coordinate /
 * SensorMath / RandomUtil / TimeUtil）盘活为对外可用的航线里程测量与船位推算能力。</p>
 */
@Service
@RequiredArgsConstructor
public class GeoMetricsService {

    /** 1 海里 = 1852 米 */
    private static final double METER_PER_NM = 1852.0;

    private final RoutePointService routePointService;
    private final RouteService routeService;

    /**
     * 两点间大圆距离（海里），复用 GpsCalculator 的 haversine 实现
     */
    public double haversineNm(double lat1, double lon1, double lat2, double lon2) {
        return GpsCalculator.distance(lat1, lon1, lat2, lon2) / METER_PER_NM;
    }

    /**
     * 按 pointOrder 逐段累加实测某条航线的里程，并与 routes.distance_nm 申报值对比
     */
    public Map<String, Object> measureRoute(Long routeId) {

        Route route = routeService.getById(routeId);

        LambdaQueryWrapper<RoutePoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoutePoint::getRouteId, routeId)
                .orderByAsc(RoutePoint::getPointOrder);

        List<RoutePoint> points = routePointService.list(wrapper);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("routeId", routeId);
        result.put("routeName", route == null ? null : route.getRouteName());
        result.put("pointCount", points.size());

        if (points.size() < 2) {
            result.put("computedDistanceNm", 0.0);
            result.put("declaredDistanceNm", route == null ? null : route.getDistanceNm());
            result.put("diffNm", null);
            result.put("segments", new ArrayList<Map<String, Object>>());
            result.put("message", "航点不足 2 个，无法测量");
            return result;
        }

        double total = 0;
        List<Map<String, Object>> segments = new ArrayList<>();

        for (int i = 0; i < points.size() - 1; i++) {

            RoutePoint a = points.get(i);
            RoutePoint b = points.get(i + 1);

            double segNm = haversineNm(
                    a.getLatitude(), a.getLongitude(),
                    b.getLatitude(), b.getLongitude());

            total += segNm;

            Map<String, Object> seg = new LinkedHashMap<>();
            seg.put("fromOrder", a.getPointOrder());
            seg.put("toOrder", b.getPointOrder());
            seg.put("distanceNm", round(segNm, 3));
            segments.add(seg);
        }

        Double declared = route == null ? null : route.getDistanceNm();

        result.put("computedDistanceNm", round(total, 3));
        result.put("declaredDistanceNm", declared);
        result.put("diffNm", declared == null ? null : round(total - declared, 3));
        result.put("segments", segments);
        return result;
    }

    /**
     * 船位推算：当前位置 + 航向 + 航速（节）+ 分钟数 → 未来坐标
     *
     * <p>核心推进使用 GeoUtil.move（WGS84 球面公式，Coordinate 载体），
     * GPS 漂移噪声由 SensorMath.gpsNoise 叠加，演示工具簇的真实能力。</p>
     */
    public Map<String, Object> advancePosition(double latitude,
                                               double longitude,
                                               double headingDeg,
                                               double speedKnots,
                                               double minutes) {

        double distanceMeter = speedKnots * METER_PER_NM * (minutes / 60.0);

        Coordinate moved = GeoUtil.move(
                new Coordinate(latitude, longitude),
                headingDeg,
                distanceMeter);

        double noisyLat = SensorMath.gpsNoise(moved.getLatitude());
        double noisyLon = SensorMath.gpsNoise(moved.getLongitude());

        // 两点间真航向校验，复用 GpsCalculator.bearing
        double bearing = GpsCalculator.bearing(
                latitude, longitude, noisyLat, noisyLon);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("from", coord(latitude, longitude));
        result.put("headingDeg", headingDeg);
        result.put("speedKnots", speedKnots);
        result.put("minutes", minutes);
        result.put("travelledNm", round(distanceMeter / METER_PER_NM, 3));
        result.put("to", coord(noisyLat, noisyLon));
        result.put("bearingDeg", round(bearing, 2));
        result.put("sampledAt", TimeUtil.format(LocalDateTime.now()));
        // 演示随机扰动能力：给一个 ±0.5° 的罗盘抖动示例
        result.put("compassJitterDeg", round(RandomUtil.randomOffset(0.5), 3));
        return result;
    }

    /**
     * 对一组有序点位做总里程 + 平均段长统计（供前端图表直接消费）
     */
    public Map<String, Object> summarizePoints(List<double[]> latLngPairs) {

        Map<String, Object> result = new LinkedHashMap<>();
        if (latLngPairs == null || latLngPairs.size() < 2) {
            result.put("totalNm", 0.0);
            result.put("segmentAvgNm", 0.0);
            result.put("segments", 0);
            return result;
        }

        double total = 0;
        for (int i = 0; i < latLngPairs.size() - 1; i++) {
            double[] a = latLngPairs.get(i);
            double[] b = latLngPairs.get(i + 1);
            total += haversineNm(a[0], a[1], b[0], b[1]);
        }

        int seg = latLngPairs.size() - 1;
        result.put("totalNm", round(total, 3));
        result.put("segmentAvgNm", round(total / seg, 3));
        result.put("segments", seg);
        return result;
    }

    private static Map<String, Object> coord(double lat, double lon) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("latitude", round(lat, 6));
        m.put("longitude", round(lon, 6));
        return m;
    }

    private static double round(double v, int scale) {
        double f = Math.pow(10, scale);
        return Math.round(v * f) / f;
    }

    /** 预留：排序辅助（保持 Comparator 显式可用，避免未用告警） */
    static Comparator<RoutePoint> byOrder() {
        return Comparator.comparing(RoutePoint::getPointOrder);
    }
}

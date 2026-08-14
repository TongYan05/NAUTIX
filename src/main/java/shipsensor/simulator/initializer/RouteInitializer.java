package shipsensor.simulator.initializer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import shipsensor.entity.Port;
import shipsensor.entity.Route;
import shipsensor.entity.RoutePoint;
import shipsensor.inter.PortMapper;
import shipsensor.inter.RouteMapper;
import shipsensor.inter.RoutePointMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class RouteInitializer {


    /**
     * 生成航线数量
     */
    private static final int ROUTE_COUNT = 100000;


    /**
     * RoutePoint 批量提交数量
     */
    private static final int POINT_BATCH_SIZE = 5000;


    private final RouteMapper routeMapper;

    private final RoutePointMapper routePointMapper;

    private final PortMapper portMapper;


    private final Random random = new Random();


    /**
     * 缓存 RoutePoint
     */
    private final List<RoutePoint> pointBatch = new ArrayList<>();


    @PostConstruct
    public void init() {


        Long count = routeMapper.selectCount(null);


        if (count != null && count > 0) {

            log.info("Route表已有数据，跳过初始化");

            return;
        }


        List<Port> ports = portMapper.selectList(null);


        if (ports == null || ports.size() < 20) {

            log.warn("Port数量不足，无法生成航线");

            return;
        }


        log.info("开始生成 {} 条航线，港口数量: {}", ROUTE_COUNT, ports.size());


        for (int i = 1; i <= ROUTE_COUNT; i++) {


            Port start = ports.get(random.nextInt(ports.size()));

            Port end = ports.get(random.nextInt(ports.size()));


            while (start.getId().equals(end.getId())) {

                end = ports.get(random.nextInt(ports.size()));

            }


            List<Port> routePorts =
                    buildRoute(ports, start, end);


            saveRoute(routePorts, i);


            if (i % 1000 == 0) {


                flushPointBatch();


                log.info(
                        "航线生成进度: {}/{}",
                        i,
                        ROUTE_COUNT
                );

            }

        }


        flushPointBatch();


        log.info(
                "航线生成完成，总数量: {}",
                ROUTE_COUNT
        );

    }


    /**
     * 构造一条航线
     * <p>
     * 起点
     * 中间停靠港
     * 终点
     */
    private List<Port> buildRoute(
            List<Port> ports,
            Port start,
            Port end) {


        List<Port> result =
                new ArrayList<>();


        List<Port> available =
                new ArrayList<>(ports);


        available.remove(start);

        available.remove(end);


        result.add(start);


        Port current = start;


        int stopCount =
                10 + random.nextInt(21);


        for (int i = 0;
             i < stopCount;
             i++) {


            if (available.isEmpty()) {

                break;

            }


            Port next =
                    findNearestPort(
                            available,
                            current
                    );


            if (next == null) {

                break;

            }


            result.add(next);


            available.remove(next);


            current = next;

        }


        result.add(end);


        return result;

    }

    /**
     * 保存一条航线
     * <p>
     * 注意：
     * Route使用AUTO_INCREMENT
     * 必须先insert Route
     * 拿到数据库生成的id
     * 再生成RoutePoint
     */
    private void saveRoute(
            List<Port> ports,
            int index) {


        if (ports == null || ports.size() < 2) {

            return;

        }


        Port start = ports.get(0);

        Port end =
                ports.get(ports.size() - 1);


        Route route = new Route();


        route.setRouteName(
                "Ocean Route-" + index
        );


        route.setStartPortId(
                start.getId()
        );


        route.setEndPortId(
                end.getId()
        );


        route.setDistanceNm(
                calculateDistance(
                        start,
                        end
                )
        );


        route.setDescription(
                start.getPortName()
                        +
                        " -> "
                        +
                        end.getPortName()
        );



        /*
         * 关键：
         * 这里执行insert以后
         * MyBatis-Plus会把AUTO_INCREMENT生成的id回填
         */
        routeMapper.insert(route);


        int order = 1;


        for (Port port : ports) {


            RoutePoint point =
                    new RoutePoint();


            point.setRouteId(
                    route.getId()
            );


            point.setPointOrder(
                    order++
            );


            point.setLatitude(
                    port.getLatitude()
            );


            point.setLongitude(
                    port.getLongitude()
            );


            pointBatch.add(point);



            /*
             * 达到批量大小立即提交
             */
            if (pointBatch.size()
                    >= POINT_BATCH_SIZE) {


                flushPointBatch();

            }

        }


    }


    /**
     * 批量插入RoutePoint
     */
    private void flushPointBatch() {


        if (pointBatch.isEmpty()) {

            return;

        }


        routePointMapper.insertBatch(
                pointBatch
        );


        pointBatch.clear();

    }


    /**
     * 寻找距离当前港口最近的港口
     * <p>
     * 替代Stream.min()
     * 提升大量数据生成速度
     */
    private Port findNearestPort(
            List<Port> ports,
            Port current) {


        Port nearest = null;


        double minDistance =
                Double.MAX_VALUE;


        for (Port port : ports) {


            double distance =
                    calculateDistance(
                            current,
                            port
                    );


            if (distance < minDistance) {


                minDistance = distance;


                nearest = port;

            }

        }


        return nearest;

    }


    /**
     * 经纬度距离计算
     * <p>
     * 当前保持你的计算方式
     * 后续如果需要可以替换成真实海里距离(Haversine)
     */
    private double calculateDistance(
            Port a,
            Port b) {


        double lat =
                a.getLatitude()
                        -
                        b.getLatitude();


        double lon =
                a.getLongitude()
                        -
                        b.getLongitude();


        return Math.sqrt(
                lat * lat
                        +
                        lon * lon
        );

    }


}
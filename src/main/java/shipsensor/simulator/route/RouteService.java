package shipsensor.simulator.route;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.Route;
import shipsensor.entity.RoutePoint;
import shipsensor.simulator.cache.RouteCache;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;

import java.util.List;


/**
 * 航线服务
 *
 * 负责：
 * 1. 获取航线
 * 2. 分配航线
 * 3. 控制船舶沿航线移动
 */
@Component
@RequiredArgsConstructor
public class RouteService {


    private final RouteCache routeCache;


    private final RuntimeStateManager runtimeStateManager;


    private final RouteNavigator routeNavigator;



    /**
     * 获取所有航线
     */
    public List<Route> getAllRoutes() {

        return routeCache.getRoutes();

    }



    /**
     * 获取某条航线的轨迹点
     */
    public List<RoutePoint> getRoutePoints(Long routeId) {

        return routeCache.getRoutePoints(routeId);

    }



    /**
     * 随机分配航线给船
     */
    public void assignRoute(Integer shipId,
                            Long routeId) {


        ShipRuntimeState state =
                runtimeStateManager.get(shipId);


        if (state == null) {

            return;

        }


        state.setRouteId(routeId);


        // 从第一个点开始

        state.setCurrentPoint(0);


    }



    /**
     * 更新所有船舶位置
     *
     * 每秒调用一次
     */
    public void updateShips() {

        routeNavigator.updateAll();

    }



    /**
     * 更新单个船舶位置
     */
    public void updateShip(Integer shipId) {


        ShipRuntimeState state =
                runtimeStateManager.get(shipId);


        if (state == null) {

            return;

        }


        routeNavigator.update(state);

    }



    /**
     * 查询船当前航线
     */
    public Route getCurrentRoute(Integer shipId) {


        ShipRuntimeState state =
                runtimeStateManager.get(shipId);


        if (state == null ||
                state.getRouteId() == null) {

            return null;

        }


        return routeCache.getRoutes()
                .stream()
                .filter(
                        r -> r.getId()
                                .equals(state.getRouteId())
                )
                .findFirst()
                .orElse(null);

    }

}
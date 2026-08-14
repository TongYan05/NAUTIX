package shipsensor.simulator.cache;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.Route;
import shipsensor.entity.RoutePoint;
import shipsensor.inter.RouteMapper;
import shipsensor.inter.RoutePointMapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class RouteCache {


    private final RouteMapper routeMapper;


    private final RoutePointMapper routePointMapper;



    /**
     * 所有航线
     */
    @Getter
    private final List<Route> routes =
            new java.util.concurrent.CopyOnWriteArrayList<>();




    /**
     * routeId -> route points
     */
    @Getter
    private final Map<Long,List<RoutePoint>> pointMap =
            new ConcurrentHashMap<>();





    @PostConstruct
    public void init(){

        refresh();

    }







    /**
     * 刷新缓存
     *
     * 优化：
     *
     * 原:
     * route数量 = 100000
     *
     * 查询:
     * 100000次 route_point
     *
     *
     * 新:
     * route查询一次
     * point查询一次
     */
    public void refresh(){


        long start =
                System.currentTimeMillis();



        routes.clear();

        pointMap.clear();





        /*
         * 查询全部route
         */
        List<Route> routeList =
                routeMapper.selectList(null);



        if(routeList == null ||
                routeList.isEmpty()){

            return;

        }



        routes.addAll(routeList);






        /*
         * 一次查询所有route point
         */
        List<RoutePoint> points =
                routePointMapper.selectList(null);





        if(points == null ||
                points.isEmpty()){

            return;

        }







        /*
         * 内存分组
         *
         * routeId -> List<RoutePoint>
         */
        Map<Long,List<RoutePoint>> grouped =
                points.stream()
                        .sorted(
                                (a,b)->
                                        Integer.compare(
                                                a.getPointOrder(),
                                                b.getPointOrder()
                                        )
                        )
                        .collect(
                                Collectors.groupingBy(
                                        RoutePoint::getRouteId
                                )
                        );





        pointMap.putAll(grouped);






        long cost =
                System.currentTimeMillis()
                        -
                        start;



        System.out.println(
                "RouteCache loaded. routes="
                        +
                        routes.size()
                        +
                        ", points="
                        +
                        points.size()
                        +
                        ", cost="
                        +
                        cost
                        +
                        "ms"
        );


    }








    /**
     * 随机获取航线
     */
    public Route randomRoute(){


        if(routes.isEmpty()){

            return null;

        }



        int index =
                (int)(
                        Math.random()
                                *
                                routes.size()
                );



        return routes.get(index);


    }







    /**
     * 获取航线节点
     */
    public List<RoutePoint> getRoutePoints(
            Long routeId
    ){


        return pointMap.get(routeId);


    }


}
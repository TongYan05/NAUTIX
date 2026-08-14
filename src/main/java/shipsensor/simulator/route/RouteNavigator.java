package shipsensor.simulator.route;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.RoutePoint;
import shipsensor.simulator.cache.RouteCache;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;


import java.util.List;



@Component
@RequiredArgsConstructor
public class RouteNavigator {


    private final RouteCache routeCache;


    private final RuntimeStateManager runtimeStateManager;



    /**
     * 更新所有船的位置
     */
    public void updateAll() {


        for(ShipRuntimeState state :
                runtimeStateManager.getAll()) {


            update(state);

        }

    }





    /**
     * 更新单船位置
     */
    public void update(
            ShipRuntimeState state
    ){


        if(state == null ||
                state.getRouteId()==null){

            return;

        }




        List<RoutePoint> points =
                routeCache.getRoutePoints(
                        state.getRouteId()
                );



        if(points==null ||
                points.size()<2){

            return;

        }




        Integer index =
                state.getCurrentPoint();



        if(index==null){

            index=0;

        }



        if(index >= points.size()-1){

            index=0;

        }



        RoutePoint current =
                points.get(index);



        RoutePoint next =
                points.get(index+1);




        /*
         * 第一次初始化位置
         */
        if(state.getLatitude()==0 &&
                state.getLongitude()==0){


            state.setLatitude(
                    current.getLatitude()
            );


            state.setLongitude(
                    current.getLongitude()
            );

        }





        /*
         * 根据速度计算移动距离
         *
         * speed: 海里/小时
         *
         * 每次任务60秒
         */
        double distanceNm =
                state.getSpeed()
                        /60.0;



        /*
         * 经纬度距离
         */
        double distance =
                calculateDistance(
                        state.getLatitude(),
                        state.getLongitude(),
                        next.getLatitude(),
                        next.getLongitude()
                );



        /*
         * 到达下一个点
         */
        if(distance <= distanceNm){


            state.setLatitude(
                    next.getLatitude()
            );


            state.setLongitude(
                    next.getLongitude()
            );


            state.setCurrentPoint(
                    index+1
            );


        }else{


            /*
             * 向目标点移动一小段
             */
            double ratio =
                    distanceNm/distance;



            double newLat =
                    state.getLatitude()
                            +
                            (next.getLatitude()
                                    -
                                    state.getLatitude())
                                    *
                                    ratio;



            double newLon =
                    state.getLongitude()
                            +
                            (next.getLongitude()
                                    -
                                    state.getLongitude())
                                    *
                                    ratio;



            state.setLatitude(newLat);

            state.setLongitude(newLon);


        }





        /*
         * 更新航向
         */
        state.setHeading(
                calculateHeading(
                        state.getLatitude(),
                        state.getLongitude(),
                        next.getLatitude(),
                        next.getLongitude()
                )
        );


    }






    /**
     * 计算两点距离 nm
     */
    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ){


        double dLat =
                Math.toRadians(lat2-lat1);


        double dLon =
                Math.toRadians(lon2-lon1);



        double a =
                Math.sin(dLat/2)
                        *
                        Math.sin(dLat/2)
                        +
                        Math.cos(Math.toRadians(lat1))
                                *
                                Math.cos(Math.toRadians(lat2))
                                *
                                Math.sin(dLon/2)
                                *
                                Math.sin(dLon/2);



        double c =
                2*Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1-a)
                );



        double km =
                6371*c;



        return km/1.852;

    }







    /**
     * 计算航向
     */
    private double calculateHeading(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ){


        double dx =
                lon2-lon1;


        double dy =
                lat2-lat1;



        double angle =
                Math.toDegrees(
                        Math.atan2(dx,dy)
                );



        if(angle<0){

            angle+=360;

        }


        return angle;

    }


}
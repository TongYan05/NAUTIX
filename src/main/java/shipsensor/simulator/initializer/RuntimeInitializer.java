package shipsensor.simulator.initializer;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import shipsensor.entity.Route;
import shipsensor.entity.RoutePoint;
import shipsensor.entity.ShipInfo;
import shipsensor.entity.WeatherRegion;
import shipsensor.inter.ShipInfoMapper;
import shipsensor.simulator.cache.RouteCache;
import shipsensor.simulator.cache.WeatherCache;
import shipsensor.simulator.factory.RuntimeStateFactory;
import shipsensor.simulator.runtime.RuntimeStateManager;
import shipsensor.simulator.runtime.ShipRuntimeState;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Component
@RequiredArgsConstructor
@Order(6)
public class RuntimeInitializer {


    private final ShipInfoMapper shipInfoMapper;

    private final RuntimeStateFactory runtimeStateFactory;

    private final RuntimeStateManager runtimeStateManager;

    private final WeatherCache weatherCache;

    private final RouteCache routeCache;



    @PostConstruct
    public void init(){


        List<ShipInfo> ships =
                shipInfoMapper.selectList(null);



        if(ships.isEmpty()){

            System.out.println("No ship data found.");

            return;

        }



        List<Route> routes =
                routeCache.getRoutes();



        if(routes.isEmpty()){

            routeCache.refresh();

            routes =
                    routeCache.getRoutes();

        }



        if(routes.isEmpty()){

            throw new RuntimeException(
                    "No route found"
            );

        }



        ThreadLocalRandom random =
                ThreadLocalRandom.current();



        for(ShipInfo ship : ships){


            Long weatherId = null;


            WeatherRegion weather =
                    weatherCache.randomWeather();



            if(weather != null){

                weatherId =
                        weather.getId();

            }



            ShipRuntimeState state =
                    runtimeStateFactory.create(
                            ship,
                            weatherId
                    );



            Route route =
                    routes.get(
                            random.nextInt(
                                    routes.size()
                            )
                    );



            state.setRouteId(
                    route.getId()
            );



            List<RoutePoint> points =
                    routeCache.getRoutePoints(
                            route.getId()
                    );



            if(points != null &&
                    !points.isEmpty()){


                RoutePoint first =
                        points.get(0);


                state.setLatitude(
                        first.getLatitude()
                );


                state.setLongitude(
                        first.getLongitude()
                );

            }



            runtimeStateManager.put(state);

        }



        runtimeStateManager.saveAll();



        System.out.println(
                "Runtime initialized : "
                        +
                        runtimeStateManager.size()
        );


    }


}
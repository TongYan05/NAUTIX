package shipsensor.simulator.initializer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import shipsensor.entity.WeatherRegion;
import shipsensor.inter.WeatherRegionMapper;
import shipsensor.simulator.factory.WeatherFactory;


@Component
@RequiredArgsConstructor
@Order(2)
public class WeatherInitializer {


    private final WeatherRegionMapper weatherRegionMapper;

    private final WeatherFactory weatherFactory;


    private static final int WEATHER_REGION_COUNT = 1000;



    @PostConstruct
    public void init(){


        Long count =
                weatherRegionMapper.selectCount(null);



        if(count != null && count > 0){

            System.out.println(
                    "WeatherRegion already exists"
            );

            return;

        }



        for(int i = 0;
            i < WEATHER_REGION_COUNT;
            i++){


            WeatherRegion weather =
                    weatherFactory.create(i);



            weatherRegionMapper.insert(
                    weather
            );


        }




        System.out.println(
                "WeatherRegion initialized: "
                        +
                        WEATHER_REGION_COUNT
        );


    }

}
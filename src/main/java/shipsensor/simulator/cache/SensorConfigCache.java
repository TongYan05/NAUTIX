package shipsensor.simulator.cache;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.inter.SensorConfigMapper;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



@Component
@RequiredArgsConstructor
@Order(6)
public class SensorConfigCache {


    private final SensorConfigMapper sensorConfigMapper;



    private final Map<Long, SensorConfig> cache =
            new ConcurrentHashMap<>();





    @PostConstruct
    public void load(){


        List<SensorConfig> configs =
                sensorConfigMapper.selectList(null);



        for(SensorConfig config:configs){


            cache.put(
                    config.getId(),
                    config
            );


        }





        System.out.println(
                "SensorConfig cache loaded: "
                        +
                        cache.size()
        );


    }







    public List<SensorConfig> getAll(){


        return List.copyOf(
                cache.values()
        );


    }







    public SensorConfig get(Long id){


        return cache.get(id);


    }



}
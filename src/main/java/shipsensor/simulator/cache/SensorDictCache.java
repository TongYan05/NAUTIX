package shipsensor.simulator.cache;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorDict;
import shipsensor.inter.SensorDictMapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
public class SensorDictCache {


    private final SensorDictMapper mapper;


    private final Map<Integer, SensorDict> cache =
            new ConcurrentHashMap<>();


    @PostConstruct
    public void load(){


        List<SensorDict> dicts =
                mapper.selectList(null);


        for(SensorDict dict : dicts){

            cache.put(
                    dict.getId(),
                    dict
            );

        }


        System.out.println(
                "SensorDict cache loaded: "
                        + cache.size()
        );

    }



    public SensorDict get(Integer id){

        return cache.get(id);

    }



    public List<SensorDict> getAll(){

        return List.copyOf(
                cache.values()
        );

    }



    public int size(){

        return cache.size();

    }

}
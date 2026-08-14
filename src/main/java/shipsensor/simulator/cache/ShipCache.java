package shipsensor.simulator.cache;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.ShipInfo;
import shipsensor.inter.ShipInfoMapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
public class ShipCache {


    private final ShipInfoMapper mapper;


    /**
     * shipId -> ShipInfo
     */
    private final Map<Integer, ShipInfo> cache =
            new ConcurrentHashMap<>();



    @PostConstruct
    public void load(){


        List<ShipInfo> ships =
                mapper.selectList(null);



        for(ShipInfo ship : ships){


            cache.put(
                    ship.getId(),
                    ship
            );


        }


        System.out.println(
                "Ship cache loaded: "
                        + cache.size()
        );


    }


    public void put(ShipInfo ship){

        if(ship == null || ship.getId() == null){
            return;
        }

        cache.put(
                ship.getId(),
                ship
        );

    }

    public ShipInfo get(Integer id){


        return cache.get(id);


    }



    public List<ShipInfo> getAll(){


        return List.copyOf(
                cache.values()
        );


    }



    public int size(){


        return cache.size();


    }

}
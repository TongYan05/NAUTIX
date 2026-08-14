package shipsensor.simulator.cache;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.Port;
import shipsensor.inter.PortMapper;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



@Component
@RequiredArgsConstructor
public class PortCache {


    private final PortMapper portMapper;



    private final Map<Long, Port> CACHE =
            new ConcurrentHashMap<>();



    public void load(){


        CACHE.clear();


        portMapper.selectList(null)
                .forEach(
                        port ->
                                CACHE.put(
                                        port.getId(),
                                        port
                                )
                );

    }




    public Port get(Long id){

        return CACHE.get(id);

    }



    public Map<Long,Port> getAll(){

        return CACHE;

    }


}
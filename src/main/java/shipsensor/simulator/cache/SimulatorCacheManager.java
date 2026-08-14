package shipsensor.simulator.cache;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.SensorDict;
import shipsensor.entity.ShipInfo;

import java.util.Collections;
import java.util.List;


/**
 * 模拟器缓存统一管理入口
 *
 * 负责：
 * 1. 提供船舶缓存访问
 * 2. 提供传感器配置访问
 * 3. 提供传感器字典访问
 *
 * 不负责数据库加载
 * 每个 Cache 自己初始化
 */
@Component
@RequiredArgsConstructor
public class SimulatorCacheManager {


    private final ShipCache shipCache;

    private final SensorConfigCache sensorConfigCache;

    private final SensorDictCache sensorDictCache;



    /**
     * 获取船舶
     */
    public ShipInfo getShip(Integer shipId){

        return shipCache.get(shipId);

    }



    /**
     * 获取全部船舶
     */
    public List<ShipInfo> getAllShips(){

        return shipCache.getAll();

    }



    /**
     * 获取某艘船的所有传感器配置
     */
    public List<SensorConfig> getSensorConfigs(
            Integer shipId
    ){

        if(shipId == null){

            return Collections.emptyList();

        }


        return sensorConfigCache
                .getAll()
                .stream()
                .filter(
                        config ->
                                shipId.equals(
                                        config.getShipId()
                                )
                )
                .toList();

    }




    /**
     * 根据传感器类型ID获取字典
     */
    public SensorDict getSensorDict(
            Integer typeId
    ){

        return sensorDictCache.get(typeId);

    }




    /**
     * 缓存统计
     */
    public void printStatistics(){

        System.out.println(
                "Simulator Cache:"
        );


        System.out.println(
                "Ships : "
                        + shipCache.size()
        );


        System.out.println(
                "SensorConfigs : "
                        + sensorConfigCache
                        .getAll()
                        .size()
        );


        System.out.println(
                "SensorDict : "
                        + sensorDictCache.size()
        );

    }

}
package shipsensor.simulator.generator;


import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



@Component
public class GeneratorManager {



    private final Map<String, SensorGenerator> generatorMap =
            new ConcurrentHashMap<>();




    public GeneratorManager(
            List<SensorGenerator> generators
    ){


        for(SensorGenerator generator : generators){


            generatorMap.put(
                    generator.getTypeCode(),
                    generator
            );


        }



        System.out.println(
                "Generator loaded : "
                        + generatorMap.keySet()
        );


    }





    public SensorGenerator getGenerator(
            String typeCode
    ){


        return generatorMap.get(
                typeCode
        );


    }



}
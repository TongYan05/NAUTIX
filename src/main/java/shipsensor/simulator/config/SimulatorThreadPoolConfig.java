package shipsensor.simulator.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Configuration
public class SimulatorThreadPoolConfig {


    /**
     * 数据生成线程池
     *
     * 根据CPU核心数自动调整
     */
    @Bean
    public ExecutorService simulatorExecutor(){


        int processors =
                Runtime.getRuntime()
                        .availableProcessors();


        System.out.println(
                "Simulator thread pool size = "
                        + processors
        );


        return Executors.newFixedThreadPool(
                processors
        );

    }


}
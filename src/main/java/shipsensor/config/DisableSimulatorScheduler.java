package shipsensor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 禁用模拟器调度器 - 确保所有 @Scheduled 方法不会执行
 */
@Configuration
public class DisableSimulatorScheduler implements SchedulingConfigurer {

    @Override
    public void configureTasks(org.springframework.scheduling.config.ScheduledTaskRegistrar taskRegistrar) {
        // 创建一个大小为 0 的线程池，让所有定时任务无法执行
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(0);
        scheduler.setThreadNamePrefix("disabled-simulator-");
        scheduler.afterPropertiesSet();

        taskRegistrar.setTaskScheduler(scheduler);
    }
}
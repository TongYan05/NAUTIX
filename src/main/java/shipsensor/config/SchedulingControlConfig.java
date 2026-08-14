package shipsensor.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * When simulator.enabled is false (or not set),
 * replace the default TaskScheduler with a no-op scheduler.
 * This prevents all @Scheduled methods from firing
 * without needing to modify each scheduled class.
 */
@Configuration
public class SchedulingControlConfig {

    @Bean
    @ConditionalOnProperty(name = "simulator.enabled", havingValue = "false", matchIfMissing = true)
    public TaskScheduler disabledTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("disabled-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }
}

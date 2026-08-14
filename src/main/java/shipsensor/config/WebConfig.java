package shipsensor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC global configuration.
 * NOTE: CORS is handled in SecurityConfig (security layer) so that browser
 * preflight (OPTIONS) requests are answered before authorization rules run.
 * Do NOT register CORS here as well, otherwise the browser receives
 * duplicate Access-Control-Allow-Origin headers and rejects the response.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
}

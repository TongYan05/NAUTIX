package shipsensor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * 跨域配置 — 已禁用，CORS 统一由 SecurityConfig 处理。
 * 此文件的 allowedOrigins 只写了 5173 死端口，会与 SecurityConfig 冲突导致所有跨域请求失败。
 */
// @Configuration  // disabled: SecurityConfig already handles CORS with allowedOriginPatterns("http://localhost:*")
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的前端域名
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        
        // 允许所有请求头
        config.addAllowedHeader("*");
        
        // 允许所有 HTTP 方法
        config.addAllowedMethod("*");
        
        // 允许携带认证信息（cookies、authorization headers）
        config.setAllowCredentials(true);
        
        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}

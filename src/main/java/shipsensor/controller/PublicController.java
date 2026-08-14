package shipsensor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 公开健康检查接口，无需认证，供前端探测后端在线状态。
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "status", "UP",
                "timestamp", System.currentTimeMillis()
        );
    }
}

package shipsensor.controller.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shipsensor.dto.AiChatRequest;
import shipsensor.dto.AiChatResponse;
import shipsensor.service.AiAssistantService;

import java.util.Arrays;
import java.util.List;

/**
 * 智能客服控制器。
 *
 * 与前端 src/api/ai.ts 中约定的 /ai/chat 接口对接。
 * 该接口位于 Spring Security 的受保护范围内，需携带登录令牌访问，
 * 保证客服查询的业务数据不对外匿名暴露。
 */
@RestController
@RequestMapping("/ai")
public class AiAssistantController {

    @Autowired
    private AiAssistantService aiAssistantService;

    /**
     * 处理一次客服提问。
     *
     * 返回体中的 data 字段即回复正文，与前端 store 的取值方式保持一致。
     */
    @PostMapping("/chat")
    public AiChatResponse chat(@RequestBody AiChatRequest request) {
        String message = request != null ? request.getMessage() : null;
        String lang = request != null ? request.getLang() : null;
        return aiAssistantService.chat(message, lang);
    }

    /**
     * 提供开场白与常见问题，供前端客服面板首次打开时展示。
     * lang=en 时返回英文问题列表。
     */
    @GetMapping("/suggestions")
    public List<String> suggestions(@RequestParam(required = false) String lang) {
        if (lang != null && lang.toLowerCase().startsWith("en")) {
            return Arrays.asList(
                    "How many ships are on the platform?",
                    "What are the latest alerts?",
                    "What is 远洋-00000's current status?",
                    "Which sensor types are supported?",
                    "What alert rules exist?",
                    "What time is it?",
                    "What is 128 * 7?",
                    "How many miles is 5 km?",
                    "What day is it today?");
        }
        return Arrays.asList(
                "平台有多少艘船？",
                "最近的告警有哪些？",
                "远洋-00000号 现在什么状态？",
                "传感器类型有哪些？",
                "告警规则有哪些？",
                "现在几点了？",
                "128乘以7等于多少？",
                "5公里是多少英里？",
                "今天星期几？");
    }
}

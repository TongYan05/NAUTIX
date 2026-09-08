package shipsensor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 智能客服响应体。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiChatResponse {

    /**
     * 客服回复正文
     */
    private String data;

    /**
     * 命中的意图标识，便于前端做差异化展示
     */
    private String intent;

    /**
     * 推荐的后续追问，前端渲染为快捷按钮
     */
    private List<String> suggestions;
}

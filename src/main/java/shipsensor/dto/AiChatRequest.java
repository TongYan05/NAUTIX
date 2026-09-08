package shipsensor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 智能客服请求体。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiChatRequest {

    /**
     * 用户输入的原始问题
     */
    private String message;

    /**
     * 回复语言："zh"（默认）或 "en"。
     */
    private String lang;
}

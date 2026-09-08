package shipsensor.service;

import shipsensor.dto.AiChatResponse;

/**
 * 智能客服服务接口。
 *
 * 基于平台真实业务数据（船舶、传感器、告警、航线、港口、天气）回答问题，
 * 不依赖任何外部大模型服务，保证离线可用且回答内容与库中数据一致。
 * 同时具备日常生活问答能力（时间、计算、换算、常识）。
 */
public interface AiAssistantService {

    /**
     * 处理一次客服提问。
     *
     * @param message 用户原始问题
     * @param lang    回复语言："zh"（默认）或 "en"
     * @return 客服回复
     */
    AiChatResponse chat(String message, String lang);
}

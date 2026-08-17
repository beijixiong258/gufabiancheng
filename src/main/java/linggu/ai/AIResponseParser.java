package linggu.ai;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import linggu.common.CommonException;
import linggu.enums.DuihuaZhuangtai;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AIResponseParser {
    private final ObjectMapper objectMapper;
    private boolean geshiJiaoyan(JsonNode jsonNode) {
        if (jsonNode == null || !jsonNode.isObject() || jsonNode.size() != 3) {
            return false;
        }
        if (!jsonNode.has("zhuangtai") || !jsonNode.has("yindao") || !jsonNode.has("quanwen")) {
            return false;
        }
        return jsonNode.get("zhuangtai").isInt()
                && (jsonNode.get("yindao").isNull() || jsonNode.get("yindao").isTextual())
                && (jsonNode.get("quanwen").isNull() || jsonNode.get("quanwen").isTextual());
    }
    public AIChatResponse jiexi(String aiYuanshiHuida) {
        try {
            JsonNode jsonNode = objectMapper.reader()
                    .with(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
                    .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                    .readTree(aiYuanshiHuida);
            if (!geshiJiaoyan(jsonNode)) {
                throw new IllegalArgumentException("AI回复字段非法。");
            }
            AIChatResponse aiChatResponse = objectMapper.treeToValue(jsonNode, AIChatResponse.class);
            if (!huidaJiaoyan(aiChatResponse)) {
                throw new IllegalArgumentException("AI回复字段组合非法。");
            }
            return aiChatResponse;
        }
        catch (Exception e) {
            throw new CommonException(500, "内部错误，AI回复获取失败。");
        }
    }

    public DuihuaZhuangtai zhuanhuan(Integer zhuangtaiCode) {
        for (DuihuaZhuangtai zhuangtai : DuihuaZhuangtai.values()) {
            if (zhuangtai.getCode() == zhuangtaiCode) {
                return zhuangtai;
            }
        }
        throw new CommonException(500, "内部错误，AI回复状态非法。");
    }
    private boolean huidaJiaoyan(AIChatResponse aiChatResponse) {
        if (aiChatResponse == null || aiChatResponse.getZhuangtai() == null) {
            return false;
        }
        if (aiChatResponse.getZhuangtai() == DuihuaZhuangtai.OVER.getCode()) {
            String quanwen = aiChatResponse.getQuanwen();
            return StrUtil.isBlank(aiChatResponse.getYindao())
                    && StrUtil.isNotBlank(quanwen)
                    && quanwen.length() <= 2000;
        }
        if (aiChatResponse.getZhuangtai() == DuihuaZhuangtai.CONTINUE.getCode()
                || aiChatResponse.getZhuangtai() == DuihuaZhuangtai.WAIT.getCode()) {
            return StrUtil.isNotBlank(aiChatResponse.getYindao())
                    && StrUtil.isBlank(aiChatResponse.getQuanwen());
        }
        return false;
    }
}

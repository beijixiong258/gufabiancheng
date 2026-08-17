package linggu.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import linggu.common.CommonException;
import linggu.enums.DuihuaZhuangtai;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AIResponseParserTest {
    private final AIResponseParser aiResponseParser = new AIResponseParser(new ObjectMapper());

    // 验证三种合法AI响应都能正确解析和转换状态。
    @ParameterizedTest
    @MethodSource("validAiResponses")
    void acceptsValidResponses(String json, DuihuaZhuangtai expectedStatus) {
        AIChatResponse response = aiResponseParser.jiexi(json);

        assertThat(aiResponseParser.zhuanhuan(response.getZhuangtai())).isEqualTo(expectedStatus);
    }

    // 验证候选正文恰好2000个字符时可以通过校验。
    @Test
    void acceptsCandidateWith2000Characters() {
        String json = "{\"zhuangtai\":0,\"yindao\":null,\"quanwen\":\""
                + "中".repeat(2000)
                + "\"}";

        AIChatResponse response = aiResponseParser.jiexi(json);

        assertThat(response.getQuanwen()).hasSize(2000);
    }

    // 验证候选正文超过2000个字符时会被拒绝。
    @Test
    void rejectsCandidateWith2001Characters() {
        String json = "{\"zhuangtai\":0,\"yindao\":null,\"quanwen\":\""
                + "中".repeat(2001)
                + "\"}";

        assertThatThrownBy(() -> aiResponseParser.jiexi(json))
                .isInstanceOf(CommonException.class);
    }

    // 验证缺少必要字段的响应会被拒绝。
    @Test
    void rejectsResponseWithMissingField() {
        assertInvalid("{\"zhuangtai\":1,\"yindao\":\"请补充信息\"}");
    }

    // 验证包含额外字段的响应会被拒绝。
    @Test
    void rejectsResponseWithExtraField() {
        assertInvalid("{\"zhuangtai\":1,\"yindao\":\"请补充信息\",\"quanwen\":null,\"extra\":1}");
    }

    // 验证字段类型错误的响应会被拒绝。
    @Test
    void rejectsResponseWithInvalidFieldType() {
        assertInvalid("{\"zhuangtai\":\"CONTINUE\",\"yindao\":\"请补充信息\",\"quanwen\":null}");
    }

    // 验证状态和字段组合错误的响应会被拒绝。
    @Test
    void rejectsResponseWithInvalidFieldCombination() {
        assertInvalid("{\"zhuangtai\":1,\"yindao\":\"请补充信息\",\"quanwen\":\"不应存在\"}");
    }

    // 验证包含重复字段的响应会被拒绝。
    @Test
    void rejectsResponseWithDuplicateField() {
        assertInvalid("{\"zhuangtai\":1,\"yindao\":\"问题一\",\"yindao\":\"问题二\",\"quanwen\":null}");
    }

    // 验证合法JSON后追加解释文本时会被拒绝。
    @Test
    void rejectsTrailingText() {
        assertInvalid("{\"zhuangtai\":1,\"yindao\":\"请补充信息\",\"quanwen\":null}额外说明");
    }

    // 验证合法JSON后追加第二个JSON值时会被拒绝。
    @Test
    void rejectsTrailingJsonValue() {
        assertInvalid("{\"zhuangtai\":1,\"yindao\":\"请补充信息\",\"quanwen\":null}{} ");
    }

    // 提供三种合法AI响应及其预期状态。
    static Stream<Arguments> validAiResponses() {
        return Stream.of(
                Arguments.of(
                        "{\"zhuangtai\":1,\"yindao\":\"请补充时间信息\",\"quanwen\":null}",
                        DuihuaZhuangtai.CONTINUE
                ),
                Arguments.of(
                        "{\"zhuangtai\":2,\"yindao\":\"请选择下一步\",\"quanwen\":null}",
                        DuihuaZhuangtai.WAIT
                ),
                Arguments.of(
                        "{\"zhuangtai\":0,\"yindao\":null,\"quanwen\":\"候选正文\"}",
                        DuihuaZhuangtai.OVER
                )
        );
    }

    // 断言指定AI响应会触发统一异常。
    private void assertInvalid(String json) {
        assertThatThrownBy(() -> aiResponseParser.jiexi(json))
                .isInstanceOf(CommonException.class);
    }
}

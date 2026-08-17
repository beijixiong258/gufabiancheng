package linggu.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import linggu.common.CommonException;
import linggu.dto.DuihuaDTO;
import linggu.entity.Huihua;
import linggu.entity.Jilu;
import linggu.entity.Xiaoxi;
import linggu.enums.DuihuaZhuangtai;
import linggu.enums.Ticai;
import linggu.enums.XiaoxiLeixing;
import linggu.service.HuihuaService;
import linggu.service.JiluService;
import linggu.service.XiaoxiService;
import linggu.vo.DuihuaVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AIChatServiceTest {
    private ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;
    private XiaoxiService xiaoxiService;
    private HuihuaService huihuaService;
    private JiluService jiluService;
    private ChatMemory chatMemory;
    private AIChatService aiChatService;

    // 初始化AI对话测试所需的依赖和模拟对象。
    @BeforeEach
    void setUp() {
        chatClientBuilder = mock(ChatClient.Builder.class);
        chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
        xiaoxiService = mock(XiaoxiService.class);
        huihuaService = mock(HuihuaService.class);
        jiluService = mock(JiluService.class);
        chatMemory = mock(ChatMemory.class);

        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(huihuaService.chakan("user-1", "huihua-1"))
                .thenReturn(new Huihua("huihua-1", "jilu-1"));
        when(jiluService.chakan("user-1", "jilu-1"))
                .thenReturn(new Jilu()
                        .setId("jilu-1")
                        .setTimu("测试记录")
                        .setTicai(Ticai.QITA)
                        .setBiaoqian("学习"));
        when(chatMemory.get("huihua-1"))
                .thenReturn(List.of(new UserMessage("用户补充的信息")));
        when(xiaoxiService.xinjian(
                anyString(), anyString(), anyString(), any(XiaoxiLeixing.class)))
                .thenAnswer(invocation -> {
                    XiaoxiLeixing type = invocation.getArgument(3, XiaoxiLeixing.class);
                    return new Xiaoxi()
                            .setId(type == XiaoxiLeixing.USER ? 101L : 102L)
                            .setHuihuaId(invocation.getArgument(1))
                            .setNeirong(invocation.getArgument(2))
                            .setType(type);
                });

        aiChatService = new AIChatService(
                chatClientBuilder,
                xiaoxiService,
                huihuaService,
                jiluService,
                chatMemory,
                new AIResponseParser(new ObjectMapper())
        );
    }

    // 验证普通对话可以接受三种合法AI状态。
    @ParameterizedTest
    @MethodSource("validAiResponses")
    void regularConversationAcceptsAllValidStatuses(String aiJson, DuihuaZhuangtai expectedStatus) {
        DuihuaVO result = call(0, aiJson);

        assertThat(result.getZhuangtai()).isEqualTo(expectedStatus);
        assertThat(result.getAiHuida()).isNotNull();
        verify(xiaoxiService).xinjian(
                "user-1",
                "huihua-1",
                "用户补充的信息",
                XiaoxiLeixing.USER
        );
        verify(chatMemory).get("huihua-1");
    }

    // 验证继续补充命令只接受CONTINUE状态且不保存按钮消息。
    @Test
    void continueCommandRequiresContinueStatus() {
        DuihuaVO result = call(
                1,
                "{\"zhuangtai\":1,\"yindao\":\"请继续补充一个细节\",\"quanwen\":null}"
        );

        assertThat(result.getZhuangtai()).isEqualTo(DuihuaZhuangtai.CONTINUE);
        verify(xiaoxiService, never()).xinjian(
                anyString(), anyString(), anyString(), eq(XiaoxiLeixing.USER));
    }

    // 验证继续补充命令会拒绝OVER状态。
    @Test
    void continueCommandRejectsOverStatus() {
        assertThatThrownBy(() -> call(
                1,
                "{\"zhuangtai\":0,\"yindao\":null,\"quanwen\":\"生成的正文\"}"
        )).isInstanceOf(CommonException.class);
    }

    // 验证直接生成命令只接受OVER状态且不保存按钮消息。
    @Test
    void generateCommandRequiresOverStatus() {
        DuihuaVO result = call(
                2,
                "{\"zhuangtai\":0,\"yindao\":null,\"quanwen\":\"完整的候选正文\"}"
        );

        assertThat(result.getZhuangtai()).isEqualTo(DuihuaZhuangtai.OVER);
        assertThat(result.getAiHuida().getNeirong()).isEqualTo("完整的候选正文");
        verify(xiaoxiService, never()).xinjian(
                anyString(), anyString(), anyString(), eq(XiaoxiLeixing.USER));
    }

    // 验证直接生成命令会拒绝CONTINUE状态。
    @Test
    void generateCommandRejectsContinueStatus() {
        assertThatThrownBy(() -> call(
                2,
                "{\"zhuangtai\":1,\"yindao\":\"请再补充一点\",\"quanwen\":null}"
        )).isInstanceOf(CommonException.class);
    }

    // 验证AI调用失败后会补偿删除本次用户消息。
    @Test
    void removesUserMessageWhenAiCallFails() {
        when(chatClient.prompt()
                .messages(anyList())
                .call()
                .content())
                .thenThrow(new RuntimeException("模拟AI调用失败"));

        assertThatThrownBy(() -> aiChatService.duihua("user-1", request(0)))
                .isInstanceOf(CommonException.class);

        verify(xiaoxiService).shanchu("user-1", "huihua-1", 101L);
        verify(xiaoxiService, never()).xinjian(
                anyString(), anyString(), anyString(), eq(XiaoxiLeixing.ASSISTANT));
    }

    // 验证协议解析失败后会补偿删除本次用户消息。
    @Test
    void removesUserMessageWhenResponseParsingFails() {
        assertThatThrownBy(() -> call(
                0,
                "{\"zhuangtai\":1,\"yindao\":\"缺少正文为空字段\"}"
        )).isInstanceOf(CommonException.class);

        verify(xiaoxiService).shanchu("user-1", "huihua-1", 101L);
        verify(xiaoxiService, never()).xinjian(
                anyString(), anyString(), anyString(), eq(XiaoxiLeixing.ASSISTANT));
    }

    // 提供三种合法AI响应及其预期状态。
    static Stream<Arguments> validAiResponses() {
        return Stream.of(
                Arguments.of(
                        "{\"zhuangtai\":1,\"yindao\":\"请补充时间信息\",\"quanwen\":null}",
                        DuihuaZhuangtai.CONTINUE
                ),
                Arguments.of(
                        "{\"zhuangtai\":2,\"yindao\":\"你希望继续补充还是直接生成？\",\"quanwen\":null}",
                        DuihuaZhuangtai.WAIT
                ),
                Arguments.of(
                        "{\"zhuangtai\":0,\"yindao\":null,\"quanwen\":\"这是生成的完整正文\"}",
                        DuihuaZhuangtai.OVER
                )
        );
    }

    // 模拟AI返回指定JSON并执行一次对话。
    private DuihuaVO call(int command, String aiJson) {
        when(chatClient.prompt()
                .messages(anyList())
                .call()
                .content())
                .thenReturn(aiJson);

        return aiChatService.duihua("user-1", request(command));
    }

    // 构造指定命令的测试请求。
    private DuihuaDTO request(int command) {
        return new DuihuaDTO()
                .setHuihuaId("huihua-1")
                .setCommand(command)
                .setNeirong(command == 0 ? "用户补充的信息" : null)
                .setZhengwen("当前已经写好的正文");
    }
}

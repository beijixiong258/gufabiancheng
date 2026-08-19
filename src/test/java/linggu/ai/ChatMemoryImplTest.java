package linggu.ai;

import linggu.entity.Xiaoxi;
import linggu.enums.XiaoxiLeixing;
import linggu.mapper.XiaoxiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatMemoryImplTest {
    private XiaoxiMapper xiaoxiMapper;
    private ChatMemoryImpl chatMemory;

    // 初始化持久化ChatMemory测试所需的Mapper模拟对象。
    @BeforeEach
    void setUp() {
        xiaoxiMapper = mock(XiaoxiMapper.class);
        chatMemory = new ChatMemoryImpl(xiaoxiMapper);
    }

    // 验证读取历史时会恢复升序并转换三种消息角色。
    @Test
    void getsMessagesInAscendingOrderWithCorrectRoles() {
        when(xiaoxiMapper.selectList(any())).thenReturn(new ArrayList<>(List.of(
                xiaoxi(1L, XiaoxiLeixing.USER, "用户消息"),
                xiaoxi(2L, XiaoxiLeixing.ASSISTANT, "AI消息"),
                xiaoxi(3L, XiaoxiLeixing.SYSTEM, "系统消息")
        )));

        List<Message> messages = chatMemory.get("huihua-1");

        assertThat(messages).extracting(Message::getMessageType)
                .containsExactly(MessageType.USER, MessageType.ASSISTANT, MessageType.SYSTEM);
        assertThat(messages).extracting(Message::getText)
                .containsExactly("用户消息", "AI消息", "系统消息");
    }

    @Test
    void summarizesOldMessagesAndKeepsRecentMessages() {
        List<Xiaoxi> messages = new ArrayList<>();
        for (long id = 1; id <= 15; id++) {
            messages.add(xiaoxi(id, id % 2 == 0 ? XiaoxiLeixing.ASSISTANT : XiaoxiLeixing.USER, "消息" + id));
        }
        when(xiaoxiMapper.selectList(any())).thenReturn(messages);

        List<Message> result = chatMemory.get("huihua-1");

        assertThat(result).hasSize(13);
        assertThat(result.get(0)).isInstanceOf(SystemMessage.class);
        assertThat(result.get(0).getText()).contains("历史摘要", "消息1");
        assertThat(result.get(result.size() - 1).getText()).isEqualTo("消息15");
    }

    // 验证写入ChatMemory时会把三种Spring AI消息转换为消息实体。
    @Test
    void addsMessagesWithCorrectTypes() {
        chatMemory.add("huihua-1", List.of(
                new UserMessage("用户消息"),
                new AssistantMessage("AI消息"),
                new SystemMessage("系统消息")
        ));

        ArgumentCaptor<Xiaoxi> captor = ArgumentCaptor.forClass(Xiaoxi.class);
        verify(xiaoxiMapper, times(3)).insert(captor.capture());
        assertThat(captor.getAllValues()).extracting(Xiaoxi::getHuihuaId)
                .containsOnly("huihua-1");
        assertThat(captor.getAllValues()).extracting(Xiaoxi::getType)
                .containsExactly(XiaoxiLeixing.USER, XiaoxiLeixing.ASSISTANT, XiaoxiLeixing.SYSTEM);
        assertThat(captor.getAllValues()).extracting(Xiaoxi::getNeirong)
                .containsExactly("用户消息", "AI消息", "系统消息");
    }

    // 验证清空ChatMemory时会调用Mapper删除指定会话的消息。
    @Test
    void clearsConversationMessages() {
        chatMemory.clear("huihua-1");

        verify(xiaoxiMapper).delete(any());
    }

    // 构造指定内容和类型的消息实体。
    private Xiaoxi xiaoxi(Long id, XiaoxiLeixing type, String neirong) {
        return new Xiaoxi()
                .setId(id)
                .setHuihuaId("huihua-1")
                .setType(type)
                .setNeirong(neirong);
    }
}

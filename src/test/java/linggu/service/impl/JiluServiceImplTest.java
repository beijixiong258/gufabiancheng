package linggu.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import linggu.entity.Jilu;
import linggu.enums.JiluZhuangtai;
import linggu.enums.Ticai;
import linggu.mapper.JiluMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JiluServiceImplTest {
    private JiluMapper jiluMapper;
    private JiluServiceImpl jiluService;

    @BeforeEach
    void setUp() {
        jiluMapper = mock(JiluMapper.class);
        jiluService = new JiluServiceImpl();
        ReflectionTestUtils.setField(jiluService, "baseMapper", jiluMapper);
    }

    // 验证记录列表使用分页查询并返回前端需要的分页信息。
    @Test
    void returnsPagedRecordList() {
        Jilu jilu = new Jilu()
                .setId("jilu-1")
                .setYonghuId("yonghu-1")
                .setTimu("旅行日记")
                .setTicai(Ticai.RIJI)
                .setBiaoqian("旅行")
                .setJiluZhuangtai(JiluZhuangtai.DRAFT)
                .setChuangjianShijian(LocalDateTime.of(2026, 8, 19, 12, 0))
                .setXiugaiShijian(LocalDateTime.of(2026, 8, 19, 13, 0));
        when(jiluMapper.selectPage(any(Page.class), any(Wrapper.class))).thenAnswer(invocation -> {
            Page<Jilu> page = invocation.getArgument(0);
            page.setRecords(List.of(jilu));
            page.setTotal(21);
            return page;
        });

        var result = jiluService.chakanLiebiao(
                "yonghu-1", "旅行", Ticai.RIJI, JiluZhuangtai.DRAFT, 2, 20);

        assertThat(result.getPage()).isEqualTo(2);
        assertThat(result.getSize()).isEqualTo(20);
        assertThat(result.getTotal()).isEqualTo(21);
        assertThat(result.getPages()).isEqualTo(2);
        assertThat(result.getRecords()).singleElement().satisfies(record -> {
            assertThat(record.getId()).isEqualTo("jilu-1");
            assertThat(record.getTimu()).isEqualTo("旅行日记");
            assertThat(record.getTicai()).isEqualTo(Ticai.RIJI);
        });

        ArgumentCaptor<Page<Jilu>> pageCaptor = ArgumentCaptor.forClass(Page.class);
        org.mockito.Mockito.verify(jiluMapper).selectPage(pageCaptor.capture(), any(Wrapper.class));
        assertThat(pageCaptor.getValue().getCurrent()).isEqualTo(2);
        assertThat(pageCaptor.getValue().getSize()).isEqualTo(20);
    }

    // 验证页码和每页数量会被限制在后端允许的范围内。
    @Test
    void normalizesPageParameters() {
        when(jiluMapper.selectPage(any(Page.class), any(Wrapper.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = jiluService.chakanLiebiao("yonghu-1", null, null, null, 0, 1000);

        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(100);
        org.mockito.Mockito.verify(jiluMapper).selectPage(any(Page.class), any(Wrapper.class));
    }
}
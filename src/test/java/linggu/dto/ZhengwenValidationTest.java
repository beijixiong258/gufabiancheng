package linggu.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import linggu.enums.Ticai;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ZhengwenValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void jiluZhengwenAllows2000CharactersAndRejects2001Characters() {
        JiluXiugaiDTO dto = new JiluXiugaiDTO()
                .setTimu("测试记录")
                .setTicai(Ticai.QITA)
                .setZhengwen("中".repeat(2000));

        assertThat(messages(dto)).isEmpty();

        dto.setZhengwen("中".repeat(2001));

        assertThat(messages(dto)).containsExactly("正文长度不能超过2000个字符。");
    }

    @Test
    void duihuaZhengwenRequiresContentAndRejects2001Characters() {
        DuihuaDTO dto = new DuihuaDTO()
                .setHuihuaId("huihua-id")
                .setCommand(1)
                .setZhengwen("中".repeat(2000));

        assertThat(messages(dto)).isEmpty();

        dto.setZhengwen("中".repeat(2001));
        assertThat(messages(dto)).containsExactly("当前正文长度不能超过2000个字符。");

        dto.setZhengwen(" ");
        assertThat(messages(dto)).containsExactly("当前正文不能为空。");
    }

    private Set<String> messages(Object target) {
        return validator.validate(target).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}

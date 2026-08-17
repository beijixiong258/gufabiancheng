package linggu.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum XiaoxiLeixing {
    USER("USER"),
    ASSISTANT("ASSISTANT"),
    SYSTEM("SYSTEM");

    @EnumValue
    private final String value;
}

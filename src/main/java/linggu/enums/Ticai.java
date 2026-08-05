package linggu.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Ticai {
    QITA(0),
    RIJI(1),
    WENXUE(2),
    XUESHU(3),
    HUIYI(4);
    @EnumValue
    private final int code;
}

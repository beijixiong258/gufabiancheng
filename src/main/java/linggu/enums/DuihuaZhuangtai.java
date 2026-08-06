package linggu.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DuihuaZhuangtai {
    OVER(0),
    CONTINUE(1),
    WAIT(2);
    @EnumValue
    private final int code;
}

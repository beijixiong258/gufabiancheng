package linggu.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Zhuangtai {
    DRAFT(0),
    FINISH(1);
    @EnumValue
    private final int code;
}

package linggu.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum Quanxian {
    USER(0),
    ADMIN(1);
    @EnumValue
    private final int code;
}

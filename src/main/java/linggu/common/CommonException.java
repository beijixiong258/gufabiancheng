package linggu.common;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException{
    private final int code;
    public CommonException(int code, String message) {
        super(message);
        this.code = code;
    }
}

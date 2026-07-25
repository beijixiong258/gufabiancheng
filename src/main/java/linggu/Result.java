package linggu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Result<T> {
    private int code;
    private T data;
    private String message;
    public static<E> Result<E> success(E data){//带数据返回成功响应
        return new Result<>(200,data,"success");
    }
    public static <E> Result<E> success(){//不带数据返回成功响应
        return success(null);
    }
    public static <E> Result<E> fail(int code,String message){//返回失败响应
        return new Result<>(code,null,message);
    }
}

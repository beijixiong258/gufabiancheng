package linggu.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理手动抛出的普通异常（运行时异常）
    @ExceptionHandler(CommonException.class)
    public Result handleCommonException(CommonException e){
        return Result.fail(e.getCode(),e.getMessage());
    }

    //处理系统抛出的参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        String message= e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return Result.fail(400,message);
    }

    //处理其它异常
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        String message=e.getMessage();
        System.out.println("内部异常："+message);
        return Result.fail(500,message);
    }
}

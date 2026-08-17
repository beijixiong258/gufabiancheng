package linggu.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理手动抛出的普通异常（运行时异常）
    @ExceptionHandler(CommonException.class)
    public Result<Void> handleCommonException(CommonException e){
        return Result.fail(e.getCode(),e.getMessage());
    }
    //处理参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        String message=e.getBindingResult().getAllErrors().stream().findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("请求参数校验失败。");
        return Result.fail(400,message);
    }
    //处理违反注解异常
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().findFirst()
                .map(violation -> violation.getMessage())
                .orElse("请求参数校验失败。");
        return Result.fail(400, message);
    }
    //处理缺少请求参数异常
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = "缺少请求参数：" + e.getParameterName();
        return Result.fail(400, message);
    }
    //处理请求参数类型错误
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return Result.fail(400, "请求参数类型错误：" + e.getName());
    }
    //处理参数绑定异常
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message;
        if (e.getBindingResult().getAllErrors().isEmpty()) {
            message = "请求参数绑定失败。";
        }
        else {
            message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }
        return Result.fail(400, message);
    }
    //处理请求体JSON格式错误和枚举值非法异常
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return Result.fail(400, "请求体格式错误。");
    }
    //处理请求方法错误
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return Result.fail(400, "请求方法不支持。");
    }
    //处理Content-Type错误
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result<Void> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return Result.fail(400, "Content-Type不支持。");
    }
    //处理其它异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e){
        String message=e.getMessage();
        System.out.println("内部异常："+message);
        return Result.fail(500,message);
    }
}

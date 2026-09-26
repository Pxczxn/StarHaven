package top.pxczxn.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import top.pxczxn.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException ex) {
        return Result.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLogin(NotLoginException ex) {
        return Result.fail(401, "未登录或登录已过期");
    }

    @ExceptionHandler(NotRoleException.class)
    public Result<Void> handleNotRole(NotRoleException ex) {
        return Result.fail(403, "无权访问");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
    public Result<Void> handleValid(Exception ex) {
        String message = "参数校验失败";
        if (ex instanceof MethodArgumentNotValidException valid) {
            var fieldError = valid.getBindingResult().getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }
        } else if (ex instanceof BindException bind) {
            var fieldError = bind.getBindingResult().getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }
        } else if (ex instanceof ConstraintViolationException violation) {
            message = violation.getConstraintViolations().iterator().next().getMessage();
        }
        return Result.fail(40002, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception ex) {
        log.error("未处理异常", ex);
        return Result.fail(50000, "系统繁忙，请稍后重试");
    }
}

package com.yzh.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 *
 * @author alick
 * @since 2023/1/12
 */
// @ControllerAdvice 注解来对annotation含有的注解执行异常处理
@ControllerAdvice(annotations = {RestController.class,Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandle {

    /**
     * 异常处理方法
     *
     * @return {@link R}<{@link String}>
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandle(CustomException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
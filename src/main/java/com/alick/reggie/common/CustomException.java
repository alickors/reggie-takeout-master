package com.alick.reggie.common;

/**
 * 自定义业务异常
 *
 * @author alick
 * @since 2023/1/13
 */
public class CustomException extends RuntimeException{

    public CustomException(String message){
        super(message);
    }
}

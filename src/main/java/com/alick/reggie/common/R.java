package com.alick.reggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用的返回结果类，服务端响应的数据最终都会封装成此对象
 * 返回值中存在泛型要在返回值类型之前加上<T>
 * @param <T>
 */
@Data
/**
 * @Data 注解的主要作用是提高代码的简洁，使用这个注解可以省去代码中大量的get()、 set()、 toString()等方法
 * 要使用 @Data 注解要先引入lombok，lombok 是什么，它是一个工具类库，可以用简单的注解形式来简化代码，提高开发效率。
 */
public class R<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}

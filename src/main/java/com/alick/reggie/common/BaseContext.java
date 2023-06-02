package com.alick.reggie.common;

/**
 * ThreadLocal保存用户id
 * ThreadLocal并不是一个Thread，而是Thread的局部变量。当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的
 * 变量副本，所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
 *
 * ThreadLocal为每个线程提供单独一份存储空间，具有线程隔离的效果，只有在线程内才能获取到对应的值，线程外则不能访问当前线程对应的值。
 * ThreadLocal常用方法：
 *
 * A. public void set(T value) : 设置当前线程的线程局部变量的值
 *
 * B. public T get() : 返回当前线程所对应的线程局部变量的值
 *
 * C. public void remove() : 删除当前线程所对应的线程局部变量的值
 *
 * 我们可以在LoginCheckFilter的doFilter方法中获取当前登录用户id，并调用ThreadLocal的set方法来设置当前线程的线程局部变量的值
 * （用户id），然后在MyMetaObjectHandler的updateFill方法中调用ThreadLocal的get方法来获得当前线程所对应的线程局部变量的值
 * （用户id）。 如果在后续的操作中, 我们需要在Controller / Service中要使用当前登录用户的ID, 可以直接从ThreadLocal直接获取。
 *
 * @author alick
 * @since 2023/1/12
 */
public class BaseContext {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrent(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrent(){
        return threadLocal.get();
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}

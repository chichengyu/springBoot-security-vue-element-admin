package com.site.common.exception;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 自定义断言
 */
public class Assert {

    /**
     * 是否为空，抛出异常
     * @param o 判断对象
     */
    public static void assertNotNull(Object o){
        if (o == null || "".equals(o)){
            throw new IllegalArgumentException();
        }
    }

    /**
     * 是否为空，抛出异常
     * @param o 判断对象
     * @param message 异常信息
     */
    public static void assertNotNull(Object o,String message){
        if (o == null || "".equals(o)){
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 是否为空，抛出异常
     * @param o 判断对象
     * @param messageSupplier supplier for the exception message
     */
    public static void assertNotNull(Object o, Supplier<String> messageSupplier){
        if (o == null || "".equals(o)){
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 是否为空，抛出异常
     * @param o 判断对象
     * @param e 异常对象
     */
    public static void assertNotNull(Object o,RuntimeException e){
        if (o == null || "".equals(o)){
            throw e;
        }
    }

    /**
     * 是否为空，抛出异常
     * @param o 判断对象
     * @param e 异常对象
     */
    public static void assertNotNull(Object o,Throwable e){
        if (o == null || "".equals(o)){
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 是否为true，抛出异常
     * @param expression 表达式
     */
    public static void isTure(boolean expression){
        if (!expression){
            throw new IllegalArgumentException();
        }
    }

    /**
     * 是否为true，抛出异常
     * @param expression 表达式
     * @param messageSupplier supplier for the exception message
     */
    public static void isTure(boolean expression, Supplier<String> messageSupplier){
        if (!expression){
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 是否为true，抛出异常
     * @param expression 表达式
     * @param e 异常对象
     */
    public static void isTure(boolean expression,RuntimeException e) {
        if (!expression){
            throw e;
        }
    }

    /**
     * 集合是否为空，抛出异常
     * @param collection 集合
     * @param message 异常信息
     */
    public static void assertNotEmpty(Collection<?> collection, String message) {
        if (isEmpty(collection)){
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 集合是否为空，抛出异常
     * @param collection 集合
     * @param messageSupplier supplier for the exception message
     */
    public static void assertNotEmpty(Collection<?> collection, Supplier<String> messageSupplier) {
        if (isEmpty(collection)){
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 集合是否为空，抛出异常
     * @param collection 集合
     * @param e 异常对象
     */
    public static void assertNotEmpty(Collection<?> collection, RuntimeException e) {
        if (isEmpty(collection)){
            throw e;
        }
    }

    /**
     * 集合是否为空，抛出异常
     * @param map 集合
     * @param message 异常信息
     */
    public static void assertNotEmpty(Map<?,?> map, String message) {
        if (isEmpty(map)){
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 集合是否为空，抛出异常
     * @param map 集合
     * @param messageSupplier supplier for the exception message
     */
    public static void assertNotEmpty(Map<?,?> map, Supplier<String> messageSupplier) {
        if (isEmpty(map)){
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 集合是否为空，抛出异常
     * @param map 集合
     * @param e 异常对象
     */
    public static void assertNotEmpty(Map<?,?> map, RuntimeException e) {
        if (isEmpty(map)){
            throw e;
        }
    }

    /**
     * 集合是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }

    /**
     * 集合是否为空
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?,?> map){
        return map == null || map.isEmpty();
    }

    private static String nullSafeGet(Supplier<String> messageSupplier) {
        return (messageSupplier != null ? messageSupplier.get() : null);
    }
}

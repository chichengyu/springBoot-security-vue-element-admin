package com.site.cache;

/**
 * 获取单个缓存的函数式接口
 */
@FunctionalInterface
public interface EntityCache<T> {

    T getCache();
}

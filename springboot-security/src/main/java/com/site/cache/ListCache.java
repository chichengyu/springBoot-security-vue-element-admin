package com.site.cache;

import java.util.List;

/**
 * 获取集合缓存的函数式接口
 */
@FunctionalInterface
public interface ListCache<T> {

    List<T> getCache();
}

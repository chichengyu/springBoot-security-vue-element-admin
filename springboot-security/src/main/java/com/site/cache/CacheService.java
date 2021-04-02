package com.site.cache;

import com.alibaba.fastjson.JSON;
import com.site.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 函数式接口实现类
 */
@Component
public class CacheService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 查询单个缓存
     * @param key  key
     * @param timeout 缓存时长
     * @param clazz 实体类
     * @param fn 单个函数式接口
     * @param <T>
     * @return
     */
    public <T>T getEntityCache(String key, Long timeout, Class<T> clazz, EntityCache<T> fn){
        T obj;
        // 先从缓存中获取
        String value = redisUtil.get(key);
        if (value == null || "".equals(value)){
            // 从数据库查询
            obj = fn.getCache();
            if (obj != null && obj != ""){
                // 存到缓存
                redisUtil.set(key, JSON.toJSONString(obj),timeout);
            }else{
                // 防止缓存穿透
                redisUtil.set(key,null,timeout);
            }
        }else{
            obj = JSON.parseObject(value,clazz);
        }
        return obj;
    }

    /**
     * 查询集合缓存
     * @param key key
     * @param timeout 缓存时长
     * @param clazz 实体类
     * @param fn 集合函数式接口
     * @param <T>
     * @return
     */
    public <T>List<T> getListCache(String key, Long timeout, Class<T> clazz, ListCache<T> fn){
        List<T> list;
        // 从缓存获取
        String value = redisUtil.get(key);
        if (value == null || "".equals(value)){
            // 从数据库查询
            list = fn.getCache();
            if (list != null && !list.isEmpty()){
                // 存到缓存
                redisUtil.set(key,JSON.toJSONString(list),timeout);
            }else{
                // 防止缓存穿透
                redisUtil.set(key,null,timeout);
            }
        }else {
            list = JSON.parseArray(value,clazz);
        }
        return list;
    }
}

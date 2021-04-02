package com.site.util;

import org.apache.commons.lang3.ObjectUtils;
import java.util.UUID;

/**
 * number工具
 */
public class NumberUtil {

    /**
     * 生成UUID
     * @return
     */
    public static String generateUUID(){
        return (UUID.randomUUID().toString()).replaceAll("-","");
    }

    /**
     * 生成UUID
     * @param prefix 前缀
     * @return
     */
    public static String generateUUID(String prefix){
        String uuid = (UUID.randomUUID().toString()).replaceAll("-","");
        if (!ObjectUtils.isEmpty(prefix)) {
            uuid = prefix + "-" + uuid;
        }
        return uuid;
    }
}

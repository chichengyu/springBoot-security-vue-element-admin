package com.site.common.base;

import com.mybatis.pj.mapper.BaseExampleMapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 通用dao，所有dao只需要继承该接口，这里tk.mapper的扩展 BaseExampleMapper已经继承了 Mapper<T>，所以不用再次继承
 * @param <T>
 */
public interface IBaseDao<T> extends /*Mapper<T>,*/ MySqlMapper<T>, BaseExampleMapper<T> {
}

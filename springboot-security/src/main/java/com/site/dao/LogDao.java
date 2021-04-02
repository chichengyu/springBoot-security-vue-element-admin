package com.site.dao;

import com.site.common.base.IBaseDao;
import com.site.pojo.SysLog;

import java.util.List;

public interface LogDao extends IBaseDao<SysLog> {

    /**
     * 批量删除日志
     * @param logIds
     * @return
     */
    int batchDeletedLog(List<String> logIds);
}

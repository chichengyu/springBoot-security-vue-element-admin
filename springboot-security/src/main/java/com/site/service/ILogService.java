package com.site.service;

import com.site.common.R;
import com.site.common.base.IBaseService;
import com.site.pojo.SysLog;

import java.util.List;

public interface ILogService extends IBaseService<SysLog> {

    /**
     * 批量删除日志
     * @param logIds
     * @return
     */
    R<String> deletedLog(List<String> logIds);
}

package com.site.service.impl;

import com.site.common.R;
import com.site.common.enums.ResponseCode;
import com.site.common.exception.BusinessException;
import com.site.common.base.impl.BaseServiceImpl;
import com.site.dao.LogDao;
import com.site.pojo.SysLog;
import com.site.service.ILogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("logService")
public class LogServiceImpl extends BaseServiceImpl<SysLog> implements ILogService {

    @Autowired
    private LogDao logDao;

    /**
     * 批量删除日志
     * @param logIds
     * @return
     */
    @Override
    public R<String> deletedLog(List<String> logIds) {
        int resultCount = logDao.batchDeletedLog(logIds);
        if (resultCount == 0){
            throw new BusinessException(ResponseCode.ERROR);
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }
}

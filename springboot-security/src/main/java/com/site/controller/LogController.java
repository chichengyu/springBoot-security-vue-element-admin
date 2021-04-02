package com.site.controller;

import com.site.common.PageResult;
import com.site.common.R;
import com.site.common.annotation.Log;
import com.site.pojo.SysLog;
import com.site.service.ILogService;
import com.site.vo.req.LogPageReqVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Api(value = "系统管理-日志管理",tags = "日志管理相关接口")
@RestController
@RequestMapping("/api")
public class LogController extends BaseController{

    @Autowired
    private ILogService logService;

    @ApiOperation(value = "分页查找操作日志",notes = "分页查找操作日志接口")
    @PreAuthorize("hasAuthority('sys:log:list')")
    @PostMapping("/logs")
    public R<PageResult<SysLog>> pageInfo(@RequestBody LogPageReqVo logPageReqVo){
        Example where = new Example(SysLog.class);
        //where.orderBy("createTime").desc();
        where.orderBy(SysLog.Fields.createTime).desc();
        return R.ok(logService.findPage(logPageReqVo.getPageNum(),logPageReqVo.getPageSize(),where));
    }

    @ApiOperation(value = "删除日志",notes = "删除日志接口")
    @PreAuthorize("hasAuthority('sys:log:delete')")
    @Log(title = "系统管理-日志管理",action = "删除日志接口")
    @DeleteMapping("/log")
    public R<String> deletedLog(@RequestBody @ApiParam(value = "日志id集合") List<String> logIds){
        return logService.deletedLog(logIds);
    }
}

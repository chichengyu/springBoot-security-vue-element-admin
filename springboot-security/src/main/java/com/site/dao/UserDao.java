package com.site.dao;

import com.site.common.base.IBaseDao;
import com.site.pojo.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao extends IBaseDao<SysUser> {

    /**
     * 批量/删除用户接口
     * @param sysUser
     * @param list
     * @return
     */
    int deletedUsers(@Param("sysUser") SysUser sysUser,@Param("list") List<String> list);
}

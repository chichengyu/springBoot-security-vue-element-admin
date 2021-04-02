package com.site.service;

import com.site.common.R;
import com.site.common.base.IBaseService;
import com.site.pojo.SysUser;
import com.site.vo.req.UserAddReqVo;
import com.site.vo.req.UserOwnRoleReqVo;
import com.site.vo.req.UserUpdateDetailInfoReqVo;
import com.site.vo.req.UserUpdatePasswordReqVo;
import com.site.vo.req.UserUpdateReqVo;
import com.site.vo.resp.UserOwnRoleRespVo;
import com.site.vo.resp.UserRespVo;

import java.util.List;

public interface IUserService extends IBaseService<SysUser> {

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    SysUser getUserByUserName(String username);

    /**
     * 新增用户
     * @param userAddReqVo
     * @return
     */
    R<String> createUser(UserAddReqVo userAddReqVo);

    /**
     * 查询用户拥有的角色数据接口
     * @param userId
     * @return
     */
    R<UserOwnRoleRespVo> getUserOwnRole(String userId);

    /**
     * 更新用户角色
     * @param userOwnRoleReqVo
     * @return
     */
    public R<String> setUserOwnRole(UserOwnRoleReqVo userOwnRoleReqVo);

    /**
     * 重置用户密码
     * @param userId
     * @return
     */
    public R<String> resetUpdatePassword(String userId);

    /**
     * 更新用户信息
     * @param userUpdateReqVo
     * @param operationId 操作人
     * @return
     */
    public R<String> updateUserInfo(UserUpdateReqVo userUpdateReqVo, String operationId);

    /**
     * 批量/删除用户接口
     * @param list
     * @param operationId 操作人
     * @return
     */
    public R<String> deletedUsers(List<String> list, String operationId);

    /**
     * 获取个人资料编辑信息
     * @param userId
     * @return
     */
    public R<UserRespVo> detailInfo(String userId);

    /**
     * 保存个人信息接口
     * @param updateDetailInfoReqVo
     * @param userId
     * @return
     */
    public R<String> userUpdateDetailInfo(UserUpdateDetailInfoReqVo updateDetailInfoReqVo, String userId);

    /**
     * 修改个人密码
     * @param userUpdatePasswordReqVo
     * @param userId
     * @return
     */
    public R<String> userUpdatePassword(UserUpdatePasswordReqVo userUpdatePasswordReqVo, String userId);
}

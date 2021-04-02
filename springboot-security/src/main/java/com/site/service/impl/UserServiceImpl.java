package com.site.service.impl;

import com.google.common.collect.Lists;
import com.site.common.R;
import com.site.common.enums.ResponseCode;
import com.site.common.exception.BusinessException;
import com.site.common.base.impl.BaseServiceImpl;
import com.site.dao.RoleDao;
import com.site.dao.UserDao;
import com.site.dao.UserRoleDao;
import com.site.pojo.SysUser;
import com.site.pojo.SysUserRole;
import com.site.service.IUserService;
import com.site.util.IdWorker;
import com.site.vo.req.UserAddReqVo;
import com.site.vo.req.UserOwnRoleReqVo;
import com.site.vo.req.UserUpdateDetailInfoReqVo;
import com.site.vo.req.UserUpdatePasswordReqVo;
import com.site.vo.req.UserUpdateReqVo;
import com.site.vo.resp.UserOwnRoleRespVo;
import com.site.vo.resp.UserRespVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<SysUser> implements IUserService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RoleDao roleDao;

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Override
    public SysUser getUserByUserName(String username) {
        return userDao.selectOne(SysUser.builder().username(username).build());
    }

    /**
     * 新增用户
     * @param userAddReqVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> createUser(UserAddReqVo userAddReqVo) {
        SysUser sysUser = this.getUserByUserName(userAddReqVo.getUsername());
        if (sysUser != null){
            throw new BusinessException(ResponseCode.ACCOUNT_EXISTS_ERROR);
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userAddReqVo,user);
        user.setId(String.valueOf(idWorker.nextId()));
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(userAddReqVo.getPassword()));
        user.setCreateTime(new Date());
        int result = this.insert(user);
        if (result != 1){
            throw new BusinessException(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 查询用户拥有的角色数据接口
     * @param userId
     * @return
     */
    @Override
    public R<UserOwnRoleRespVo> getUserOwnRole(String userId) {
        UserOwnRoleRespVo userOwnRoleRespVo = new UserOwnRoleRespVo();
        userOwnRoleRespVo.setOwnRoleIds(userRoleDao.getRoleIdsByUserId(userId));
        userOwnRoleRespVo.setAllRole(roleDao.selectAll());
        return R.ok(userOwnRoleRespVo);
    }

    /**
     * 更新用户角色
     * @param userOwnRoleReqVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> setUserOwnRole(UserOwnRoleReqVo userOwnRoleReqVo) {
        // 更新用户角色关联表信息
        if (userOwnRoleReqVo.getUserId() == null){
            throw new BusinessException(ResponseCode.DATA_ERROR);
        }
        // 先删除用户原来的拥有的角色id
        userRoleDao.removeRoleIdsByUserId(userOwnRoleReqVo.getUserId());
        if (userOwnRoleReqVo.getRoleIds().isEmpty()){// 为空表示去除该用户所有角色
            return R.ok(ResponseCode.SUCCESS.getMessage());
        }
        // 批量插入用户角色数据
        Date date = new Date();
        List<SysUserRole> list = Lists.newArrayList();
        for (String roleId:userOwnRoleReqVo.getRoleIds()){
            SysUserRole sysUserRole = SysUserRole.builder().id(String.valueOf(idWorker.nextId()))
                    .roleId(roleId)
                    .userId(userOwnRoleReqVo.getUserId())
                    .createTime(date)
                    .build();
            list.add(sysUserRole);
        }
        int result = userRoleDao.insertList(list);
        if (result == 0){
            throw new BusinessException(ResponseCode.ERROR);
        }
        /**
         * 标记用户需要重新登录,禁止再访问我们的系统资源
         */
        /*redisService.set(Constant.JWT_USER_LOGIN_BLACKLIST+vo.getUserId(),vo.getUserId());*/
        /**
         * 清楚用户授权数据缓存
         */
        /*redisService.delete(Constant.IDENTIFY_CACHE_KEY+vo.getUserId());*/
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 重置用户密码
     * @param userId
     * @return
     */
    @Override
    public R<String> resetUpdatePassword(String userId) {
        SysUser sysUser = this.findOne(userId);
        if (userId == null){
            throw new BusinessException(ResponseCode.DATA_ERROR);
        }
        SysUser user = SysUser.builder().id(userId).password(BCrypt.hashpw("123456", BCrypt.gensalt())).build();
        int result = this.update(user);
        if (result == 0){
            throw new BusinessException(ResponseCode.ERROR);
        }
        /**
         * 标记用户需要重新登录,禁止再访问我们的系统资源
         */
        //redisService.set(Constant.JWT_USER_LOGIN_BLACKLIST + userId,userId);
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 更新用户信息
     * @param userUpdateReqVo
     * @param operationId 操作人
     * @return
     */
    @Override
    public R<String> updateUserInfo(UserUpdateReqVo userUpdateReqVo, String operationId) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userUpdateReqVo,sysUser);
        sysUser.setUpdateId(operationId);
        sysUser.setUpdateTime(new Date());
        if (StringUtils.isBlank(userUpdateReqVo.getPassword())){
            sysUser.setPassword(StringUtils.EMPTY);
        }else{
            sysUser.setPassword(BCrypt.hashpw(userUpdateReqVo.getPassword(),BCrypt.gensalt()));
        }
        int result = this.update(sysUser);
        if (result == 0){
            throw new BusinessException(ResponseCode.ERROR);
        }
        /*if(userUpdateReqVo.getStatus() == 2){
            // 账号锁定需要更新 redis 账号锁定状态
            redisService.set(Constant.ACCOUNT_LOCK_KEY+sysUser.getId(),sysUser.getId());
        }else {
            // 删除账号锁定状态
            redisService.delete(Constant.ACCOUNT_LOCK_KEY+sysUser.getId());
        }*/
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 批量/删除用户接口
     * @param list
     * @param operationId 操作人
     * @return
     */
    @Override
    public R<String> deletedUsers(List<String> list, String operationId) {
        // 更新操作人
        SysUser sysUser = new SysUser();
        sysUser.setUpdateId(operationId);
        sysUser.setUpdateTime(new Date());
        int result = userDao.deletedUsers(sysUser,list);
        if (result == 0){
            throw new BusinessException(ResponseCode.ERROR);
        }
        //for (String userId : list){
            /**
             * 标记用户需要重新登录,禁止再访问我们的系统资源
             */
            //redisService.delete(Constant.JWT_USER_LOGIN_BLACKLIST + userId);
            /**
             * 更新 redis 中 账号的删除状态
             */
            //redisService.set(Constant.DELETED_USER_KEY + userId,userId,tokenConfig.getAccessTokenExpireTime().toMillis(),TimeUnit.MILLISECONDS);
        //}
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 获取个人资料编辑信息
     * @param userId
     * @return
     */
    @Override
    public R<UserRespVo> detailInfo(String userId) {
        if (StringUtils.isBlank(userId)){
            return R.error(ResponseCode.DATA_ERROR.getMessage());
        }
        SysUser sysUser = this.findOne(userId);
        UserRespVo userRespVo = new UserRespVo();
        BeanUtils.copyProperties(sysUser,userRespVo);
        return R.ok(userRespVo);
    }

    /**
     * 保存个人信息接口
     * @param updateDetailInfoReqVo
     * @param userId
     * @return
     */
    @Override
    public R<String> userUpdateDetailInfo(UserUpdateDetailInfoReqVo updateDetailInfoReqVo, String userId){
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(updateDetailInfoReqVo,sysUser);
        sysUser.setId(userId);
        sysUser.setUpdateId(userId);
        sysUser.setUpdateTime(new Date());
        int updateCount = this.update(sysUser);
        if (updateCount != 1){
            throw new BusinessException(ResponseCode.ERROR);
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 修改个人密码
     * @param userUpdatePasswordReqVo
     * @param userId
     * @return
     */
    @Override
    public R<String> userUpdatePassword(UserUpdatePasswordReqVo userUpdatePasswordReqVo, String userId){
        if (StringUtils.isBlank(userId)){
            throw new BusinessException(ResponseCode.DATA_ERROR);
        }
        SysUser sysUser = this.findOne(userId);
        if (sysUser == null){
            throw new BusinessException(ResponseCode.TOKEN_ERROR);
        }
        if (!BCrypt.checkpw(userUpdatePasswordReqVo.getOldPwd(),sysUser.getPassword())){
            throw new BusinessException(ResponseCode.OLD_PASSWORD_ERROR);
        }
        sysUser.setUpdateId(userId);
        sysUser.setPassword(BCrypt.hashpw(userUpdatePasswordReqVo.getNewPwd(),BCrypt.gensalt()));
        sysUser.setUpdateTime(new Date());
        int updateCount = this.update(sysUser);
        if (updateCount != 1){
            throw new BusinessException(ResponseCode.ERROR);
        }
        /**
         * 标记用户需要重新登录,禁止再访问我们的系统资源
         */
        //redisService.set(Constant.JWT_USER_LOGIN_BLACKLIST+userId,userId);
        /**
         * 清楚用户授权数据缓存
         */
        //redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }
}

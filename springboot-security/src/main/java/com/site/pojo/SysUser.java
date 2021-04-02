package com.site.pojo;

import com.site.vo.resp.PermissionRespNodeVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser implements UserDetails {

    private static final long serialVersionUID = -4021990931172884026L;
    @Id
    private String id;
    private String username;
    private String password;
    private String phone;
    private String realName;
    private String nickName;
    private String email;
    private Integer status;
    private Integer sex;
    private Integer deleted;
    private String createId;
    private String updateId;
    private Long createWhere;
    private Date createTime;
    private Date updateTime;

    //帐户是否过期(1 未过期，0已过期)
    @Transient
    private boolean isAccountNonExpired = true;
    //帐户是否被锁定(1 未锁定，0已锁定)
    @Transient
    private boolean isAccountNonLocked = true;
    //密码是否过期(1 未过期，0已过期)
    @Transient
    private boolean isCredentialsNonExpired = true;
    //帐户是否可用(1 可用，0 删除用户)
    @Transient
    private boolean isEnabled = true;
    @Transient
    private Collection<? extends GrantedAuthority> authorities;
    // 用于登录成功返回用户权限菜单
    @Transient
    List<PermissionRespNodeVo> menus;
    // 前端按钮权限标识
    @Transient
    List<String> permissions;
}

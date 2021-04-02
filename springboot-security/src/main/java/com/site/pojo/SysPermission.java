package com.site.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysPermission implements Serializable {

    private static final long serialVersionUID = -2940118009674767884L;
    @Id
    private String id;
    private String code;
    private String title;
    private String icon;
    private String perms;
    private String url;
    private String method;
    private String name;
    private String pid;
    private Long orderNum;
    private Integer type;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer deleted;

    // 用于权限列表时展示
    @Transient
    private String pidName;
}

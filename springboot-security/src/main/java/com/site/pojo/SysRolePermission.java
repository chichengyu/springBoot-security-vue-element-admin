package com.site.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysRolePermission implements Serializable {

    private static final long serialVersionUID = 3810023498448594910L;
    private String id;
    private String roleId;
    private String permissionId;
    private Date createTime;
}

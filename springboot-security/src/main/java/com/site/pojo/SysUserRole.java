package com.site.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 4083649422735963021L;
    private String id;
    private String userId;
    private String roleId;
    private Date createTime;

}

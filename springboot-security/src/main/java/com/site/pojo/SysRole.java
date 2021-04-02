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
public class SysRole implements Serializable {

    private static final long serialVersionUID = 2243563036452833035L;
    @Id
    private String id;
    private String name;
    private String description;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer deleted;

}

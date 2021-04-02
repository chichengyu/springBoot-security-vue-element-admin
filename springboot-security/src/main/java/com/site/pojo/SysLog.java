package com.site.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants(onlyExplicitlyIncluded = true)
public class SysLog implements Serializable {

    private static final long serialVersionUID = -7343647009161174417L;
    @Id
    private String id;
    private String userId;
    private String username;
    private String operation;
    private Long time;
    private String method;
    private String params;
    private String ip;
    @FieldNameConstants.Include
    private Date createTime;

}

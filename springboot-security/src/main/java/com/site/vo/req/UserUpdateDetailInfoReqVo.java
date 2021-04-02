package com.site.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "用户编辑对象",description = "用户编辑信息对象")
public class UserUpdateDetailInfoReqVo {

    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "性别(1.男 2.女)")
    private Integer sex;
    @ApiModelProperty(value = "真实名称")
    private String realName;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "账户状态(1.正常 2.锁定 )")
    private Integer status;
}

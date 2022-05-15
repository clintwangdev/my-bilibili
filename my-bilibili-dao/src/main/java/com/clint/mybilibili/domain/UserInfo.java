package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "用户信息")
public class UserInfo {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "用户 ID")
    private Long userId;

    @ApiModelProperty(value = "昵称")
    private String nick;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "生日")
    private String birth;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否关注")
    private Boolean followed;
}

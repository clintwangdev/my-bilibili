package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@ApiModel(value = "用户信息")
@Document(indexName = "user-infos")
public class UserInfo {

    @ApiModelProperty(value = "主键")
    @Id
    private Long id;

    @ApiModelProperty(value = "用户 ID")
    private Long userId;

    @ApiModelProperty(value = "昵称")
    @Field(type = FieldType.Text)
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
    @Field(type = FieldType.Date)
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @Field(type = FieldType.Date)
    private Date updateTime;

    @ApiModelProperty(value = "是否关注")
    private Boolean followed;
}

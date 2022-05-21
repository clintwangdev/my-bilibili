package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "视频")
@Document(indexName = "videos")
public class Video {

    @ApiModelProperty(value = "主键")
    @Id
    private Long id;

    @ApiModelProperty(value = "用户id")
    @Field(type = FieldType.Long)
    private Long userId;

    @ApiModelProperty(value = "视频链接")
    private String url;

    @ApiModelProperty(value = "封面链接")
    private String thumbnail;

    @ApiModelProperty(value = "视频标题")
    @Field(type = FieldType.Text)
    private String title;

    @ApiModelProperty(value = "视频类型：0原创 1转载")
    private String type;

    @ApiModelProperty(value = "视频时长")
    private String duration;

    @ApiModelProperty(value = "所在分区：0鬼畜 1音乐 2电影")
    private String area;

    @ApiModelProperty(value = "视频简介")
    @Field(type = FieldType.Text)
    private String description;

    @ApiModelProperty(value = "创建时间")
    @Field(type = FieldType.Date)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @Field(type = FieldType.Date)
    private Date updateTime;

    @ApiModelProperty(value = "视频标签集合")
    private List<VideoTag> videoTagList;
}

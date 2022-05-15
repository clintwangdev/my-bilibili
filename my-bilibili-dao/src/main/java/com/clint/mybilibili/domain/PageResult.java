package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@ApiModel(value = "分页返回结果")
public class PageResult<T> {

    @ApiModelProperty(value = "分页查询结果总数")
    private Integer total;

    @ApiModelProperty(value = "数据集合")
    private List<T> list;
}

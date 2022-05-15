package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Json 数据返回类
 *
 * @param <T> 返回数据类型
 */
@Data
@ApiModel(value = "JSON 数据返回类")
public class JsonResponse<T> {

    /**
     * 状态码
     */
    @ApiModelProperty(value = "响应状态码")
    private String code;

    /**
     * 响应消息
     */
    @ApiModelProperty(value = "响应消息")
    private String msg;

    /**
     * 响应数据
     */
    @ApiModelProperty(value = "响应数据")
    private T data;

    private JsonResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private JsonResponse(T data) {
        this.data = data;
        code = "0";
        msg = "成功";
    }

    /**
     * 响应成功
     *
     * @return 无需响应数据
     */
    public static JsonResponse<String> success() {
        return new JsonResponse<>(null);
    }

    /**
     * 响应成功
     *
     * @return 响应成功数据
     */
    public static <T> JsonResponse<T> success(T data) {
        return new JsonResponse<>(data);
    }

    /**
     * 响应失败
     *
     * @return 默认失败数据
     */
    public static JsonResponse<String> fail() {
        return new JsonResponse<>("1", "失败");
    }

    /**
     * 响应失败
     *
     * @param code 状态码
     * @param msg  响应消息
     * @return 自定义失败数据
     */
    public static JsonResponse<String> fail(String code, String msg) {
        return new JsonResponse<>(code, msg);
    }
}

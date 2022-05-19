package com.clint.mybilibili.api;

import com.clint.mybilibili.service.util.FastDFSUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Api(tags = "测试接口")
public class TestApi {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @GetMapping("/slices")
    @ApiOperation(value = "文件分片")
    public void slices(MultipartFile file) throws IOException {
        fastDFSUtil.convertFileToSlices(file);
    }
}

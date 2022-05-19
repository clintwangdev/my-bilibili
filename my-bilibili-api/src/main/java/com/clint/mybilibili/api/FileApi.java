package com.clint.mybilibili.api;

import com.clint.mybilibili.domain.JsonResponse;
import com.clint.mybilibili.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Api(tags = "文件接口")
public class FileApi {

    @Autowired
    private FileService fileService;

    @PostMapping("/md5files")
    @ApiOperation(value = "获取文件MD5加密后的字符串")
    public JsonResponse<String> getFileMD5(MultipartFile file) {
        String fileMD5 = fileService.getFileMD5(file);
        return JsonResponse.success(fileMD5);
    }

    @PutMapping("/file-slices")
    @ApiOperation(value = "通过分片上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "slice", value = "文件分片", required = true),
            @ApiImplicitParam(name = "fileMD5", value = "文件唯一标识", required = true),
            @ApiImplicitParam(name = "sliceNo", value = "当前分片数", required = true),
            @ApiImplicitParam(name = "totalSliceNo", value = "总分片数", required = true),
    })
    public JsonResponse<String> uploadFileBySlices(MultipartFile slice, String fileMd5, Integer sliceNo,
                                                   Integer totalSliceNo) throws IOException {
        String filePath = fileService.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);
        return JsonResponse.success(filePath);
    }
}

package com.clint.mybilibili.api;

import com.clint.mybilibili.domain.JsonResponse;
import com.clint.mybilibili.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileApi {

    @Autowired
    private FileService fileService;

    @PutMapping("/file-slices")
    public JsonResponse<String> uploadFileBySlices(MultipartFile slice, String fileMd5, Integer sliceNo,
                                                   Integer totalSliceNo) throws IOException {
        String filePath = fileService.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);
        return JsonResponse.success(filePath);
    }
}

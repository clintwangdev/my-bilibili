package com.clint.mybilibili.service;

import com.clint.mybilibili.service.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    /**
     * 通过分片上传文件
     *
     * @param slice        文件分片
     * @param fileMd5      文件唯一标识
     * @param sliceNo      当前分片数
     * @param totalSliceNo 总分片数
     * @return
     */
    public String uploadFileBySlices(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws IOException {
        return fastDFSUtil.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);
    }
}

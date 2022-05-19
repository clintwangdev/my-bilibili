package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.FileDao;
import com.clint.mybilibili.domain.File;
import com.clint.mybilibili.service.util.FastDFSUtil;
import com.clint.mybilibili.service.util.MD5Util;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service
public class FileService {

    @Autowired
    private FileDao fileDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    /**
     * 通过分片上传文件
     *
     * @param slice        文件分片
     * @param fileMD5      文件唯一标识
     * @param sliceNo      当前分片数
     * @param totalSliceNo 总分片数
     * @return
     */
    public String uploadFileBySlices(MultipartFile slice, String fileMD5, Integer sliceNo, Integer totalSliceNo)
            throws IOException {
        // 通过 MD5 标识获取文件
        File dbFileMD5 = fileDao.getFileByMD5(fileMD5);
        if (dbFileMD5 != null) { // 如果该文件不为空
            // 返回文件 url
            return dbFileMD5.getUrl();
        }
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMD5, sliceNo, totalSliceNo);
        if (!Strings.isNullOrEmpty(url)) { // 如果上传文件成功
            // 返回 url 不为空
            dbFileMD5 = new File();
            dbFileMD5.setCreateTime(new Date());
            dbFileMD5.setMd5(fileMD5);
            dbFileMD5.setUrl(url);
            dbFileMD5.setType(fastDFSUtil.getFileType(slice));
            // 将文件信息添加到数据库
            fileDao.saveFile(dbFileMD5);
        }
        return url;
    }

    /**
     * 获取文件MD5加密后的字符串
     */
    public String getFileMD5(MultipartFile file) {
        return MD5Util.getFileMD5(file);
    }
}

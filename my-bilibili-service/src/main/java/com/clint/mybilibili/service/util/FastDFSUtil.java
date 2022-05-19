package com.clint.mybilibili.service.util;

import com.clint.mybilibili.domain.exception.ConditionException;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * FastDFS工具类
 */
@Component
public class FastDFSUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String DEFAULT_GROUP = "group1";

    private static final String PATH_KEY = "path-key:";

    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";

    private static final String UPLOADED_NO_KEY = "uploaded-no-key:";

    private static final Integer SLICE_SIZE = 1024 * 1024;

    /**
     * 获取文件类型
     */
    public String getFileType(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ConditionException("非法文件！");
        }
        String filename = file.getOriginalFilename();
        int suffixIndex = filename.lastIndexOf(".");
        String suffixName = filename.substring(suffixIndex + 1);
        return suffixName;
    }

    /**
     * 文件上传
     */
    public String uploadCommonFile(MultipartFile file) throws Exception {
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType,
                metaDataSet);
        return storePath.getPath();
    }

    /**
     * 上传可以断点续传的文件
     */
    public String uploadAppenderFile(MultipartFile file) throws IOException {
        String fileType = getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(),
                file.getSize(), fileType);
        return storePath.getPath();
    }

    /**
     * 修改文件
     */
    public void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws IOException {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }

    /**
     * 通过分片上传文件
     *
     * @param file         文件
     * @param fileMD5      文件唯一标识
     * @param sliceNo      当前分片数
     * @param totalSliceNo 总分片数
     * @return
     */
    public String uploadFileBySlices(MultipartFile file, String fileMD5, Integer sliceNo, Integer totalSliceNo)
            throws IOException {
        if (file == null || sliceNo == null || totalSliceNo == null) {
            throw new ConditionException("参数异常！");
        }
        String pathKey = PATH_KEY + fileMD5;
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMD5;
        String uploadedNoKey = UPLOADED_NO_KEY + fileMD5;
        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        long uploadedSize = 0L;
        // 获取当前文件传输了多少
        if (!StringUtils.isNullOrEmpty(uploadedSizeStr)) {
            uploadedSize = Long.parseLong(uploadedSizeStr);
        }
        String fileType = this.getFileType(file);
        if (sliceNo.equals(1)) {
            // 如果上传的是第一个分片，则进行文件上传
            String path = this.uploadAppenderFile(file);
            if (StringUtils.isNullOrEmpty(path)) {
                throw new ConditionException("上传失败！");
            }
            redisTemplate.opsForValue().set(pathKey, path);
            redisTemplate.opsForValue().set(uploadedNoKey, "1");
        } else {
            // 不是第一个分片
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if (StringUtils.isNullOrEmpty(filePath)) {
                throw new ConditionException("上传失败！");
            }
            this.modifyAppenderFile(file, filePath, uploadedSize);
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }
        // 修改历史上传分片文件大小
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));
        // 判断文件是否上传成功
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        Integer uploadedNo = Integer.parseInt(uploadedNoStr);
        String resultPath = "";
        if (uploadedNo.equals(totalSliceNo)) {
            resultPath = redisTemplate.opsForValue().get(pathKey);
            // 上传成功，清空 Redis 中相关 key
            List<String> keyList = Arrays.asList(uploadedNoKey, uploadedSizeKey, pathKey);
            redisTemplate.delete(keyList);
        }
        return resultPath;
    }

    /**
     * 将文件转为分片
     */
    public void convertFileToSlices(MultipartFile multipartFile) throws IOException {
        String fileType = this.getFileType(multipartFile);
        File file = this.multipartFileToFile(multipartFile);
        long fileLength = file.length();
        // 分片计数值
        int count = 1;
        for (int i = 0; i < fileLength; i += SLICE_SIZE) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            // 搜寻起始位置
            randomAccessFile.seek(i);
            // 读取数据
            byte[] bytes = new byte[SLICE_SIZE];
            int len = randomAccessFile.read(bytes);
            String path = "D:/projects/tmpfile/" + count + "." + fileType;
            File slice = new File(path);
            // 将分片写入磁盘
            FileOutputStream fileOutputStream = new FileOutputStream(slice);
            fileOutputStream.write(bytes, 0, len);
            fileOutputStream.close();
            randomAccessFile.close();
            ++count;
        }
        // 删除临时文件
        file.delete();
    }

    /**
     * 将 MultipartFile 转为 File
     */
    public File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String[] filename = originalFilename.split("\\.");
        File file = File.createTempFile(filename[0], "." + filename[1]);
        multipartFile.transferTo(file);
        return file;
    }

    /**
     * 删除文件
     */
    public void deleteFile(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }
}

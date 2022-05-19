package com.clint.mybilibili.dao;

import com.clint.mybilibili.domain.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileDao {

    File getFileByMD5(String fileMD5);

    Integer saveFile(File dbFileMD5);
}

package com.clint.mybilibili.dao;

import com.clint.mybilibili.domain.Video;
import com.clint.mybilibili.domain.VideoTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoDao {

    Integer saveVideos(Video video);

    Integer batchSaveVideoTags(@Param("videoTagList") List<VideoTag> videoTagList);
}

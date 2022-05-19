package com.clint.mybilibili.dao;

import com.clint.mybilibili.domain.Video;
import com.clint.mybilibili.domain.VideoLike;
import com.clint.mybilibili.domain.VideoTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface VideoDao {

    Integer saveVideos(Video video);

    Integer batchSaveVideoTags(@Param("videoTagList") List<VideoTag> videoTagList);

    Integer pageCountVideos(String area);

    List<Video> pageListVideos(Map<String, Object> params);

    Video getVideoById(Long videoId);

    VideoLike getVideoLikeByUserIdAndVideoId(@Param("userId") Long userId, @Param("videoId") Long videoId);

    Integer saveVideoLike(VideoLike videoLike);

    Integer removeVideoLike(@Param("userId") Long userId, @Param("videoId") Long videoId);

    Long getVideoLikes(Long videoId);
}

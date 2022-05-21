package com.clint.mybilibili.dao;

import com.clint.mybilibili.domain.*;
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

    Integer removeVideoCollection(VideoCollection videoCollection);

    Integer saveVideoCollection(VideoCollection videoCollection);

    Long getVideoCollections(Long videoId);

    VideoCollection getVideoCollectionByUserIdAndVideoId(@Param("userId") Long userId, @Param("videoId") Long videoId);

    Integer saveCollectionGroup(CollectionGroup collectionGroup);

    List<CollectionGroup> getUserCollectionGroups(Long userId);

    CollectionGroup getCollectionById(Long groupId);

    CollectionGroup getUserDefaultCollectionGroup(Long userId);

    VideoCoin getVideoCoinByUserIdAndVideoId(@Param("userId") Long userId, @Param("videoId") Long videoId);

    Integer saveVideoCoin(VideoCoin videoCoin);

    Integer updateVideoCoin(VideoCoin videoCoin);

    Long getVideoCoinsAmount(Long videoId);

    Integer saveVideoComment(VideoComment videoComment);

    Integer pageCountVideoComments(Long videoId);

    List<VideoComment> pageListVideoComments(@Param("params") Map<String, Object> params);

    List<VideoComment> batchGetVideoCommentsByRootIds(@Param("rootIdList") List<Long> parentIdList);

    VideoView getVideoView(Map<String, Object> params);

    Integer saveVideoView(VideoView videoView);

    Long getVideoViewCounts(Long videoId);
}

package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.VideoDao;
import com.clint.mybilibili.domain.*;
import com.clint.mybilibili.domain.exception.ConditionException;
import com.clint.mybilibili.service.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoService {

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private UserCoinService userCoinService;

    @Autowired
    private UserService userService;

    /**
     * 添加视频
     */
    @Transactional
    public void saveVideos(Video video) {
        Date now = new Date();
        video.setCreateTime(now);
        // 添加视频
        videoDao.saveVideos(video);
        // 获取添加后的视频 ID
        Long videoId = video.getId();
        // 批量添加视频标签
        List<VideoTag> videoTagList = video.getVideoTagList();
        videoTagList.forEach(item -> {
            item.setVideoId(videoId);
            item.setCreateTime(now);
        });
        videoDao.batchSaveVideoTags(videoTagList);
    }

    /**
     * 分页查询视频
     */
    public PageResult<Video> pageListVideos(Integer no, Integer size, String area) {
        if (no == null || size == null) {
            throw new ConditionException("参数异常！");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("area", area);
        List<Video> videoList = new ArrayList<>();
        // 获取视频总条数
        Integer total = videoDao.pageCountVideos(area);
        if (total > 0) { // 如果视频数大于0
            videoList = videoDao.pageListVideos(params);
        }
        PageResult<Video> pageResult = new PageResult<Video>(total, videoList);
        return pageResult;
    }

    /**
     * 通过分片在线播放视频
     */
    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        fastDFSUtil.viewVideoOnlineBySlices(request, response, url);
    }

    /**
     * 添加视频点赞
     */
    public void saveVideoLike(Long userId, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        // 通过用户id和视频id查询点赞视频
        VideoLike videoLike = videoDao.getVideoLikeByUserIdAndVideoId(userId, videoId);
        if (videoLike != null) {
            throw new ConditionException("已经点过赞了！");
        }
        videoLike = new VideoLike();
        videoLike.setCreateTime(new Date());
        videoLike.setUserId(userId);
        videoLike.setVideoId(videoId);
        // 添加视频点赞
        videoDao.saveVideoLike(videoLike);
    }

    /**
     * 取消视频点赞
     */
    public void removeVideoLike(Long userId, Long videoId) {
        videoDao.removeVideoLike(userId, videoId);
    }

    /**
     * 获取视频点赞数量
     */
    public Map<String, Object> getVideoLikes(Long userId, Long videoId) {
        Long count = videoDao.getVideoLikes(videoId);
        VideoLike videoLike = videoDao.getVideoLikeByUserIdAndVideoId(userId, videoId);
        boolean like = videoLike != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    /**
     * 添加视频收藏
     * x
     */
    @Transactional
    public void saveVideoCollection(VideoCollection videoCollection) {
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if (videoId == null || groupId == null) {
            throw new ConditionException("参数异常！");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        // 验证收藏分组是否存在
        CollectionGroup collectionGroup = videoDao.getCollectionById(groupId);
        if (collectionGroup == null) {
            // 获取该用户默认分组ID
            CollectionGroup userCollectionGroup = videoDao.getUserDefaultCollectionGroup(videoCollection.getUserId());
            videoCollection.setGroupId(userCollectionGroup.getId());
        }
        // 删除原有视频收藏
        videoDao.removeVideoCollection(videoCollection);
        // 添加新的视频收藏
        videoCollection.setCreateTime(new Date());
        videoDao.saveVideoCollection(videoCollection);
    }

    /**
     * 删除视频收藏
     */
    public void removeVideoCollection(Long userId, Long videoId) {
        VideoCollection videoCollection = new VideoCollection();
        videoCollection.setUserId(userId);
        videoCollection.setVideoId(videoId);
        videoDao.removeVideoCollection(videoCollection);
    }

    /**
     * 获取视频收藏量
     */
    public Map<String, Object> getVideoCollections(Long videoId, Long userId) {
        Long count = videoDao.getVideoCollections(videoId);
        VideoCollection videoCollection = videoDao.getVideoCollectionByUserIdAndVideoId(userId, videoId);
        boolean like = videoCollection != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    /**
     * 添加收藏分组
     */
    public void saveCollectionGroup(CollectionGroup collectionGroup) {
        collectionGroup.setCreateTime(new Date());
        // 设为自定义分组类型
        collectionGroup.setType("1");
        videoDao.saveCollectionGroup(collectionGroup);
    }

    /**
     * 获取用户收藏分组
     */
    public List<CollectionGroup> getUserCollectionGroups(Long userId) {
        return videoDao.getUserCollectionGroups(userId);
    }

    /**
     * 为角色分配默认分组
     */
    public void saveUserDefaultCollectionGroup(Long userId) {
        CollectionGroup collectionGroup = new CollectionGroup();
        collectionGroup.setUserId(userId);
        collectionGroup.setName("默认分组");
        collectionGroup.setType("0");
        collectionGroup.setCreateTime(new Date());
        videoDao.saveCollectionGroup(collectionGroup);
    }

    /**
     * 视频投币
     */
    @Transactional
    public void saveVideoCoins(VideoCoin videoCoin) {
        Long videoId = videoCoin.getVideoId();
        Long amount = videoCoin.getAmount();
        if (videoId == null) {
            throw new ConditionException("参数异常！");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        Long userId = videoCoin.getUserId();
        // 查询当前登录用户是否拥有足够的硬币
        Long userCoinAmount = userCoinService.getUserCoinAmount(userId);
        userCoinAmount = userCoinAmount == null ? 0L : userCoinAmount;
        if (amount > userCoinAmount) {
            throw new ConditionException("硬币数量不足！");
        }
        // 查询该用户是否为视频投过币
        VideoCoin dbVideoCoin = videoDao.getVideoCoinByUserIdAndVideoId(userId, videoId);
        if (dbVideoCoin == null) {
            // 还没有投过币
            videoCoin.setCreateTime(new Date());
            videoDao.saveVideoCoin(videoCoin);
        } else {
            // 投过币了
            Long dbVideoCoinAmount = dbVideoCoin.getAmount();
            dbVideoCoinAmount += amount;
            // 更新视频投币
            videoCoin.setUpdateTime(new Date());
            videoCoin.setAmount(dbVideoCoinAmount);
            videoDao.updateVideoCoin(videoCoin);
        }
        // 更新当前用户硬币总数
        userCoinService.updateUserCoinAmount(userId, (userCoinAmount - amount));
    }

    /**
     * 获取视频投币数量
     */
    public Map<String, Object> getVideoCoins(Long userId, Long videoId) {
        Long count = videoDao.getVideoCoinsAmount(videoId);
        VideoCoin videoCoin = videoDao.getVideoCoinByUserIdAndVideoId(userId, videoId);
        boolean like = videoCoin != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    /**
     * 添加视频评论
     */
    public void saveVideoComment(VideoComment videoComment) {
        Long videoId = videoComment.getVideoId();
        if (videoId == null) {
            throw new ConditionException("参数异常！");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        videoComment.setCreateTime(new Date());
        videoDao.saveVideoComment(videoComment);
    }

    /**
     * 分页获取视频评论
     */
    public PageResult<VideoComment> pageListVideoComments(Integer no, Integer size, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("videoId", videoId);
        Integer total = videoDao.pageCountVideoComments(videoId);
        List<VideoComment> videoCommentList = new ArrayList<>();
        if (total > 0) { // 如果一级评论数量大于0
            videoCommentList = videoDao.pageListVideoComments(params);
            // 批量查询二级评论
            List<Long> parentIdList = videoCommentList.stream().map(VideoComment::getId).collect(Collectors.toList());
            List<VideoComment> childCommentList = videoDao.batchGetVideoCommentsByRootIds(parentIdList);
            // 批量查询用户信息
            Set<Long> userIdList = videoCommentList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            Set<Long> replyUserIdList = videoCommentList.stream().map(VideoComment::getReplyUserId).collect(Collectors.toSet());
            userIdList.addAll(replyUserIdList);
            List<UserInfo> userInfoList = userService.batchGetUserInfosByUserIds(userIdList);
            Map<Long, UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));
            videoCommentList.forEach(videoComment -> {
                Long id = videoComment.getId();
                List<VideoComment> childList = new ArrayList<>();
                childCommentList.forEach(child -> {
                    if (id.equals(child.getRootId())) { // 如果父id等于该评论id
                        child.setUserInfo(userInfoMap.get(child.getUserId()));
                        child.setReplyUserInfo(userInfoMap.get(child.getReplyUserId()));
                        childList.add(child);
                    }
                });
                videoComment.setChildList(childList);
                videoComment.setUserInfo(userInfoMap.get(videoComment.getUserId()));
            });
        }
        return new PageResult<>(total, videoCommentList);
    }

    /**
     * 获取视频详情
     */
    public Map<String, Object> getVideoDetails(Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        Long userId = video.getUserId();
        User user = userService.getUserInfo(userId);
        UserInfo userInfo = user.getUserInfo();
        Map<String, Object> result = new HashMap<>();
        result.put("video", video);
        result.put("userInfo", userInfo);
        return result;
    }
}

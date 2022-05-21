package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.VideoDao;
import com.clint.mybilibili.domain.*;
import com.clint.mybilibili.domain.exception.ConditionException;
import com.clint.mybilibili.service.util.FastDFSUtil;
import com.clint.mybilibili.service.util.IpUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

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

    /**
     * 添加视频观看记录
     */
    public void saveVideoView(VideoView videoView, HttpServletRequest request) {
        Long userId = videoView.getUserId();
        Long videoId = videoView.getVideoId();
        // 生成 ClientId
        String agent = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        String clientId = String.valueOf(userAgent.getId());
        String ip = IpUtil.getIP(request);
        // 用于封装参数
        Map<String, Object> params = new HashMap<>();
        if (userId != null) {
            params.put("userId", userId);
        } else {
            params.put("ip", ip);
            params.put("clientId", clientId);
        }
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        params.put("today", sdf.format(now));
        params.put("videoId", videoId);
        // 判断今天是否已经观看过该视频
        VideoView dbVideoView = videoDao.getVideoView(params);
        if (dbVideoView == null) { // 如果没有看过该视频
            // 添加观看记录
            videoView.setIp(ip);
            videoView.setClientId(clientId);
            videoView.setCreateTime(new Date());
            videoDao.saveVideoView(videoView);
        }
    }

    /**
     * 获取视频观看次数
     */
    public Long getVideoViewCounts(Long videoId) {
        return videoDao.getVideoViewCounts(videoId);
    }

    /**
     * 视频内容推荐
     */
    public List<Video> recommend(Long userId) throws TasteException {
        // 获取所有用户偏好
        List<UserPreference> list = videoDao.getAllUserPreference();
        // 创建数据模型
        DataModel dataModel = createDataModel(list);
        // 获取用户相似度
        UncenteredCosineSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
        long[] array = userNeighborhood.getUserNeighborhood(userId);
        // 构建推荐器
        Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
        // 推荐
        List<RecommendedItem> recommendedItems = recommender.recommend(userId, 5);
        List<Long> itemIds = recommendedItems.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
        return videoDao.batchGetVideosByIds(itemIds);
    }

    private DataModel createDataModel(List<UserPreference> list) {
        FastByIDMap<PreferenceArray> fastByIdMap = new FastByIDMap<>();
        Map<Long, List<UserPreference>> map = list.stream().collect(Collectors.groupingBy(UserPreference::getUserId));
        Collection<List<UserPreference>> values = map.values();
        for (List<UserPreference> userPreference : values) {
            // 用户存放用户偏好
            GenericPreference[] genericPreferences = new GenericPreference[userPreference.size()];
            for (int i = 0; i < userPreference.size(); ++i) {
                UserPreference tmpUserPreference = userPreference.get(i);
                GenericPreference item = new GenericPreference(tmpUserPreference.getUserId(),
                        tmpUserPreference.getVideoId(), tmpUserPreference.getValue());
                genericPreferences[i] = item;
            }
            fastByIdMap.put(genericPreferences[0].getUserID(), new GenericUserPreferenceArray(
                    Arrays.asList(genericPreferences)));
        }
        return new GenericDataModel(fastByIdMap);
    }
}

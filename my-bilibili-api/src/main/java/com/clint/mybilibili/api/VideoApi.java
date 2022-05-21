package com.clint.mybilibili.api;

import com.clint.mybilibili.api.support.UserSupport;
import com.clint.mybilibili.domain.*;
import com.clint.mybilibili.service.ElasticSearchService;
import com.clint.mybilibili.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api(tags = "视频接口")
@RestController
public class VideoApi {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @PostMapping("/videos")
    @ApiOperation(value = "视频投稿")
    public JsonResponse<String> saveVideos(@RequestBody Video video) {
        Long userId = userSupport.getCurrentId();
        video.setUserId(userId);
        videoService.saveVideos(video);
        // 添加到ES中
        elasticSearchService.saveVideo(video);
        return JsonResponse.success();
    }

    @GetMapping("/videos")
    @ApiOperation(value = "分页查询视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "no", value = "当前页数", required = true),
            @ApiImplicitParam(name = "size", value = "每页显示条数", required = true),
            @ApiImplicitParam(name = "nick", value = "视频所在分区：0鬼畜 1音乐 2电影")
    })
    public JsonResponse<PageResult<Video>> pageListVideos(Integer no, Integer size, String area) {
        PageResult<Video> pageResult = videoService.pageListVideos(no, size, area);
        return JsonResponse.success(pageResult);
    }

    @GetMapping("/video-slices")
    @ApiOperation(value = "在线观看视频")
    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        videoService.viewVideoOnlineBySlices(request, response, url);
    }

    @PostMapping("/video-likes")
    @ApiOperation(value = "视频点赞")
    public JsonResponse<String> saveVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentId();
        videoService.saveVideoLike(userId, videoId);
        return JsonResponse.success();
    }

    @DeleteMapping("/video-likes")
    @ApiOperation(value = "取消点赞")
    public JsonResponse<String> removeVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentId();
        videoService.removeVideoLike(userId, videoId);
        return JsonResponse.success();
    }

    @GetMapping("/video-likes")
    @ApiOperation(value = "获取视频点赞数量")
    public JsonResponse<Map<String, Object>> getVideoLikes(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentId();
        } catch (Exception ignored) {
        }
        Map<String, Object> result = videoService.getVideoLikes(userId, videoId);
        return JsonResponse.success(result);
    }


    @PostMapping("/video-collections")
    @ApiOperation(value = "添加视频收藏")
    public JsonResponse<String> saveVideoCollection(@RequestBody VideoCollection videoCollection) {
        Long userId = userSupport.getCurrentId();
        videoCollection.setUserId(userId);
        videoService.saveVideoCollection(videoCollection);
        return JsonResponse.success();
    }

    @DeleteMapping("/video-collections")
    @ApiOperation(value = "取消视频收藏")
    public JsonResponse<String> removeVideoCollection(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentId();
        videoService.removeVideoCollection(userId, videoId);
        return JsonResponse.success();
    }

    @GetMapping("/video-collections")
    @ApiOperation(value = "获取视频收藏量")
    public JsonResponse<Map<String, Object>> getVideoCollections(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentId();
        } catch (Exception ignored) {
        }
        Map<String, Object> result = videoService.getVideoCollections(videoId, userId);
        return JsonResponse.success(result);
    }

    @PostMapping("/collection-groups")
    @ApiOperation(value = "添加收藏分组")
    public JsonResponse<String> saveCollectionGroup(@RequestBody CollectionGroup collectionGroup) {
        Long userId = userSupport.getCurrentId();
        collectionGroup.setUserId(userId);
        videoService.saveCollectionGroup(collectionGroup);
        return JsonResponse.success();
    }

    @GetMapping("/collection-groups")
    @ApiOperation(value = "获取用户收藏分组")
    public JsonResponse<List<CollectionGroup>> getUserCollectionGroups() {
        Long userId = userSupport.getCurrentId();
        List<CollectionGroup> collectionGroupList = videoService.getUserCollectionGroups(userId);
        return JsonResponse.success(collectionGroupList);
    }

    @PostMapping("/video-coins")
    @ApiOperation(value = "视频投币")
    public JsonResponse<String> saveVideoCoins(@RequestBody VideoCoin videoCoin) {
        Long userId = userSupport.getCurrentId();
        videoCoin.setUserId(userId);
        videoService.saveVideoCoins(videoCoin);
        return JsonResponse.success();
    }

    @GetMapping("/video-coins")
    @ApiOperation(value = "获取视频投币数量")
    public JsonResponse<Map<String, Object>> getVideoCoins(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentId();
        } catch (Exception ignored) {
        }
        Map<String, Object> result = videoService.getVideoCoins(userId, videoId);
        return JsonResponse.success(result);
    }

    @PostMapping("/video-comments")
    @ApiOperation(value = "添加视频评论")
    public JsonResponse<String> saveVideoComment(@RequestBody VideoComment videoComment) {
        Long userId = userSupport.getCurrentId();
        videoComment.setUserId(userId);
        videoService.saveVideoComment(videoComment);
        return JsonResponse.success();
    }

    @GetMapping("/video-comments")
    @ApiOperation(value = "分页获取视频评论")
    public JsonResponse<PageResult<VideoComment>> pageListVideoComments(@RequestParam Integer no,
                                                                        @RequestParam Integer size,
                                                                        @RequestParam Long videoId) {
        PageResult<VideoComment> result = videoService.pageListVideoComments(no, size, videoId);
        return JsonResponse.success(result);
    }

    @GetMapping("/video-details")
    @ApiOperation(value = "获取视频详情")
    public JsonResponse<Map<String, Object>> getVideoDetails(@RequestParam Long videoId) {
        Map<String, Object> result = videoService.getVideoDetails(videoId);
        return JsonResponse.success(result);
    }

    /**
     * 添加视频观看记录
     */
    @PostMapping("/video-views")
    public JsonResponse<String> saveVideoView(@RequestBody VideoView videoView,
                                              HttpServletRequest request) {
        Long userId;
        try {
            userId = userSupport.getCurrentId();
            videoView.setUserId(userId);
            videoService.saveVideoView(videoView, request);
        } catch (Exception e) {
            videoService.saveVideoView(videoView, request);
        }
        return JsonResponse.success();
    }

    /**
     * 查询视频播放量
     */
    @GetMapping("/video-view-counts")
    public JsonResponse<Long> getVideoViewCounts(@RequestParam Long videoId) {
        Long count = videoService.getVideoViewCounts(videoId);
        return JsonResponse.success(count);
    }
}

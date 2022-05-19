package com.clint.mybilibili.api;

import com.clint.mybilibili.api.support.UserSupport;
import com.clint.mybilibili.domain.JsonResponse;
import com.clint.mybilibili.domain.PageResult;
import com.clint.mybilibili.domain.Video;
import com.clint.mybilibili.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Api(tags = "视频接口")
@RestController
public class VideoApi {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserSupport userSupport;

    @PostMapping("/videos")
    @ApiOperation(value = "视频投稿")
    public JsonResponse<String> saveVideos(@RequestBody Video video) {
        Long userId = userSupport.getCurrentId();
        video.setUserId(userId);
        videoService.saveVideos(video);
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
        } catch (Exception e) {
        }
        Map<String, Object> result = videoService.getVideoLikes(userId, videoId);
        return JsonResponse.success(result);
    }
}

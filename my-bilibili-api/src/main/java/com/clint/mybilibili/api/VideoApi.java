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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}

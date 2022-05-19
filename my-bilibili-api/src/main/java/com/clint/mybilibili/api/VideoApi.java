package com.clint.mybilibili.api;

import com.clint.mybilibili.api.support.UserSupport;
import com.clint.mybilibili.domain.JsonResponse;
import com.clint.mybilibili.domain.Video;
import com.clint.mybilibili.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
}

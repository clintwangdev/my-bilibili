package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.VideoDao;
import com.clint.mybilibili.domain.Video;
import com.clint.mybilibili.domain.VideoTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoDao videoDao;

    /**
     * 添加视频
     */
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
}

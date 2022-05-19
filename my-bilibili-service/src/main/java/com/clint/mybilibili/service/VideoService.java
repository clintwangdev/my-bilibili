package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.VideoDao;
import com.clint.mybilibili.domain.PageResult;
import com.clint.mybilibili.domain.Video;
import com.clint.mybilibili.domain.VideoTag;
import com.clint.mybilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class VideoService {

    @Autowired
    private VideoDao videoDao;

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
}

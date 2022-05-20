package com.clint.mybilibili.service.websocket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.clint.mybilibili.dao.DanmuDao;
import com.clint.mybilibili.domain.Danmu;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DanmuService {

    private static final String DANMU_KEY = "danmu-video-";

    @Autowired
    private DanmuDao danmuDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 添加弹幕
     */
    public void saveDanmu(Danmu danmu) {
        danmuDao.saveDanmu(danmu);
    }

    /**
     * 异步添加弹幕
     */
    @Async
    public void asyncSaveDanmu(Danmu danmu) {
        danmuDao.saveDanmu(danmu);
    }

    /**
     * 获取弹幕
     */
    public List<Danmu> getDanmus(Long videoId, String startTime, String endTime) throws ParseException {
        String key = DANMU_KEY + videoId;
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> danmuList;
        if (!StringUtils.isNullOrEmpty(value)) { // 如果Redis中有弹幕
            danmuList = JSONArray.parseArray(value, Danmu.class);
            if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
                List<Danmu> childList = new ArrayList<>();
                danmuList.forEach(danmu -> {
                    Date createTime = danmu.getCreateTime();
                    // 筛选指定时间范围类的弹幕
                    if (createTime.after(startDate) && createTime.before(endDate)) {
                        childList.add(danmu);
                    }
                });
                danmuList = childList;
            }
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("videoId", videoId);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            danmuList = danmuDao.getDanmus(params);
            // 存入Redis
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(danmuList));
        }
        return danmuList;
    }

    /**
     * 将弹幕添加到Redis
     */
    public void saveDanmuToRedis(Danmu danmu) {
        String key = DANMU_KEY + danmu.getVideoId();
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> danmuList = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(value)) { // 如果Redis中存有弹幕
            // 转为List
            danmuList = JSONArray.parseArray(value, Danmu.class);
        }
        // 将新弹幕追加
        danmuList.add(danmu);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(danmuList));
    }
}

package com.clint.mybilibili.dao;

import com.clint.mybilibili.domain.Danmu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DanmuDao {

    Integer saveDanmu(Danmu danmu);

    List<Danmu> getDanmus(Map<String, Object> params);
}

package com.clint.mybilibili.dao.repository;

import com.clint.mybilibili.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VideoRepository extends ElasticsearchRepository<Video, Long> {

    /**
     * 根据标题进行模糊查询
     */
    Video findByTitleLike(String keyword);
}

package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.repository.UserInfoRepository;
import com.clint.mybilibili.dao.repository.VideoRepository;
import com.clint.mybilibili.domain.UserInfo;
import com.clint.mybilibili.domain.Video;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ElasticSearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void saveVideo(Video video) {
        videoRepository.save(video);
    }

    public Video getVideos(String keyword) {
        return videoRepository.findByTitleLike(keyword);
    }

    public void saveUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    public List<Map<String, Object>> getContents(String keyword, Integer pageNo, Integer pageSize) throws IOException {
        String[] indices = {"videos", "user-infos"};
        SearchRequest searchRequest = new SearchRequest(indices);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNo - 1);
        sourceBuilder.size(pageSize);
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "description", "nick");
        sourceBuilder.query(matchQueryBuilder);
        // 指定查询请求的配置
        searchRequest.source(sourceBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 高亮显示
        String[] array = {"title", "description", "nick"};
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String key : array) {
            // 添加高亮字段
            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
        }
        highlightBuilder.requireFieldMatch(false);
        // 设置高亮样式
        highlightBuilder.preTags("<span style=\"color: red\"");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        // 执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            // 处理高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            for (String key : array) {
                HighlightField field = highlightFields.get(key);
                if (field != null) {
                    Text[] fragments = field.getFragments();
                    String str = Arrays.toString(fragments);
                    str = str.substring(1, str.length() - 1);
                    sourceMap.put(key, str);
                }
            }
            list.add(sourceMap);
        }
        return list;
    }
}

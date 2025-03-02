package com.analytics.social_media_analytics.com.analytics.repository;


import com.analytics.social_media_analytics.com.analytics.model.TweetDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TweetRepository extends ElasticsearchRepository<TweetDocument,String>{
}

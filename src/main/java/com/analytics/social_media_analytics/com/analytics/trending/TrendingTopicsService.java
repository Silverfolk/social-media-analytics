package com.analytics.social_media_analytics.com.analytics.trending;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TrendingTopicsService {

    private static final String TRENDING_TOPICS_KEY = "trending-topics";

    private final StringRedisTemplate redisTemplate;

    public TrendingTopicsService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void incrementTopicCount(String topic) {
        redisTemplate.opsForZSet().incrementScore(TRENDING_TOPICS_KEY, topic, 1);
    }

    public Set<String> getTopTrendingTopics(int limit) {
        return redisTemplate.opsForZSet().reverseRange(TRENDING_TOPICS_KEY, 0, limit-1);
}
}

package com.analytics.social_media_analytics.com.analytics.consumer;

import com.analytics.social_media_analytics.com.analytics.model.TweetDocument;
import com.analytics.social_media_analytics.com.analytics.sentiment.SentimentAnalysisService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.analytics.social_media_analytics.com.analytics.repository.TweetRepository;
import com.analytics.social_media_analytics.com.analytics.trending.TrendingTopicsService;

@Service
public class SentimentAnalysisConsumer {

    private final SentimentAnalysisService sentimentAnalysisService;
    private final TweetRepository tweetRepository;
    private final TrendingTopicsService trendingTopicsService;

    public SentimentAnalysisConsumer(SentimentAnalysisService sentimentAnalysisService,TweetRepository tweetRepository,TrendingTopicsService trendingTopicsService) {
        this.sentimentAnalysisService = sentimentAnalysisService;
        this.tweetRepository=tweetRepository;
        this.trendingTopicsService=trendingTopicsService;
    }

    @KafkaListener(topics = "raw-posts", groupId = "sentiment-analysis-group")
    public void consume(String tweet) {
        String sentiment = sentimentAnalysisService.analyzeSentiment(tweet);
        TweetDocument tweetDocument=new TweetDocument();
        tweetDocument.setText(tweet);
        tweetDocument.setSentiment(sentiment);
        tweetRepository.save(tweetDocument);
        String[] words = tweet.split("\\s+");
        for (String word : words) {
            if (word.startsWith("#")) {
                trendingTopicsService.incrementTopicCount(word);
            }
        }

        System.out.println("Tweet: " + tweet);
        System.out.println("Sentiment: " + sentiment);

}
}

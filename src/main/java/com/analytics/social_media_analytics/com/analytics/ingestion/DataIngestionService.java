package com.analytics.social_media_analytics.com.analytics.ingestion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

@Service
public class DataIngestionService {

    @Value("${twitter.consumer-key}")
    private String consumerKey;

    @Value("${twitter.consumer-secret}")
    private String consumerSecret;

    @Value("${twitter.access-token}")
    private String accessToken;

    @Value("${twitter.access-token-secret}")
    private String accessTokenSecret;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public DataIngestionService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 60000) // Fetch tweets every minute
    public void fetchTweets() throws TwitterException {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        Query query = new Query("#AI");
        List<Status> tweets = twitter.search(query).getTweets();

        for (Status tweet : tweets) {
            kafkaTemplate.send("raw-posts", tweet.getText());
        }
    }
}
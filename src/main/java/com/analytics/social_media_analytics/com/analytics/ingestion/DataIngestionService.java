package com.analytics.social_media_analytics.com.analytics.ingestion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
public class DataIngestionService {

    @Value("${twitter.bearer-token}") // Use Bearer Token for X API v2
    private String bearerToken;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;

    public DataIngestionService(KafkaTemplate<String, String> kafkaTemplate, RestTemplate restTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 900000) // Fetch tweets every 15 minutes (Free Tier limit)
    public void fetchTweets() {
        String query = "AI"; // Search query
        String url = "https://api.twitter.com/2/tweets/search/recent?query=" + query + "&max_results=10"; // Free Tier allows 10 results per request

        // Set up headers with Bearer Token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the HTTP GET request
        ResponseEntity<TwitterResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                TwitterResponse.class
        );

        // Process the response
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<Tweet> tweets = response.getBody().getData();
            for (Tweet tweet : tweets) {
                kafkaTemplate.send("raw-posts", tweet.getText()); // Send tweet text to Kafka
            }
        } else {
            System.err.println("Failed to fetch tweets: " + response.getStatusCode());
        }
    }

    // Define classes to map the X API v2 response
    private static class TwitterResponse {
        private List<Tweet> data;

        public List<Tweet> getData() {
            return data;
        }

        public void setData(List<Tweet> data) {
            this.data = data;
        }
    }

    private static class Tweet {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
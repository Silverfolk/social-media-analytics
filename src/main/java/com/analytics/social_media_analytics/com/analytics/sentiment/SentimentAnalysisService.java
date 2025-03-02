package com.analytics.social_media_analytics.com.analytics.sentiment;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Service
public class SentimentAnalysisService {

    private StanfordCoreNLP pipeline;

    @PostConstruct
    public void init() {
        try {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
            pipeline = new StanfordCoreNLP(props);
            System.out.println("Stanford CoreNLP pipeline initialized successfully.");
        } catch (Exception e) {
            System.err.println("Failed to initialize Stanford CoreNLP pipeline:");
            e.printStackTrace();
        }
    }

    public String analyzeSentiment(String text) {
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        StringBuilder sentimentResult = new StringBuilder();
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            sentimentResult.append(sentiment).append(" ");
        }

        return sentimentResult.toString().trim();
}
}

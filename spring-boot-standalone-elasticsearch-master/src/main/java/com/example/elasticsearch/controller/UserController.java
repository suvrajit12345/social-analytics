package com.example.elasticsearch.controller;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery.Type;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.elasticsearch.model.User;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Service;

import java.util.Properties;



@RestController
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    Client client;
    @Autowired
    SentimentAnalyzerService SentimentAnalyzerService;
  
    //saving data from twitter along with sentiment score in es
    
    @GetMapping("/save")
    public  String SentimentAnalyzerService()  throws TwitterException , IOException  {
   
       
       ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("vHH0ybs1hrQBlVZNojhzkLoZC")
		.setOAuthConsumerSecret("lEHTyP1s1AQPqftt00D5rWq4yoriiWP02qOpKPgTbEGmiXrMkB")
		.setOAuthAccessToken("1263407896135643137-RmpJS84ZQqLqGRD9UdZocpUGu0902j") 
		.setOAuthAccessTokenSecret("HkDNxysJQHhqLqYPNObNsuvXTx3IG7kAmjkTY13JM24E7");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		Query query = new Query("#verizon");
		query.setCount(100);
		QueryResult result = twitter.search(query);
		for(Status status :result.getTweets()) {
		// status.getText();
		 SentimentAnalyzerService  obj  = new SentimentAnalyzerService();
		 int k=  obj.analyse(status.getText().trim()
         		.replaceAll("http.*?[\\S]+", "")  
         		.replaceAll("@[\\S]+", "")   
         		.replaceAll("#", "")
         		.replaceAll("[\\s]+", " "));
		 
		 IndexResponse response = client.prepareIndex("twitter", "users",Integer.toString((int) status.getId()))
	                .setSource(jsonBuilder()
	                        .startObject()
	                        .field("SNumber", status.getId())
	                        .field("hashtag", "#verizon")
	                        .field("date",status.getCreatedAt() )
	                        .field("name",status.getUser().getScreenName() )
	                        .field("location",status.getUser().getLocation() )
	                        .field("text", status.getText().trim()
	                        		.replaceAll("http.*?[\\S]+", "")  
	                        		.replaceAll("@[\\S]+", "")   
	                        		.replaceAll("#", "")
	                        		.replaceAll("[\\s]+", " "))
	                        .field("sentimentScore", k )
	                        
	                               .endObject() )
	                .get();	
		 
		
		}
		
		
		return "Hey!!we will fetch the sentiments scores for you"; 
    
    }
    
    
    //getting data from twitter and saving in es
    
    @GetMapping("/get")
    public  String getTwitterinstance() throws TwitterException , IOException {
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("vHH0ybs1hrQBlVZNojhzkLoZC")
		.setOAuthConsumerSecret("lEHTyP1s1AQPqftt00D5rWq4yoriiWP02qOpKPgTbEGmiXrMkB")
		.setOAuthAccessToken(" 1263407896135643137-RmpJS84ZQqLqGRD9UdZocpUGu0902j") 
		.setOAuthAccessTokenSecret("HkDNxysJQHhqLqYPNObNsuvXTx3IG7kAmjkTY13JM24E7");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		Query query = new Query("#verizon");
		query.setCount(2);
		QueryResult result = twitter.search(query);	
		
		
		for(Status status :result.getTweets())
		{
			
	     IndexResponse response = client.prepareIndex("twitter", "users",Integer.toString((int) status.getId()))
		                .setSource(jsonBuilder()
		                        .startObject()
		                        .field("SNumber", status.getId())
		                        .field("hashtag", "#verizon")
		                        .field("date",status.getCreatedAt() )
		                        .field("name",status.getUser().getScreenName() )
		                        .field("location",status.getUser().getLocation() )
		                        .field("text", status.getText().trim()
		                        		.replaceAll("http.*?[\\S]+", "")  // remove links
		                        		.replaceAll("@[\\S]+", "")   // remove usernames
		                        		.replaceAll("#", "")// replace hashtags by just words
		                        		.replaceAll("[\\s]+", " "))
		                               .endObject() )
		                .get();	
			 			
		}
		return "data  saved  in elasticsearch";
		}
   
   
    //fetching data from es with help of location
    
    @GetMapping("/fetch/location/{value}")
    public List<Map<String, Object>> searchByLocationName(@PathVariable String value,String twitter, String users) throws TwitterException , IOException {
    	 
    	int scrollSize = 10;
    	
    	 List<Map<String,Object>> esData = new ArrayList<Map<String,Object>>();
    	
    	 SearchResponse response = null;
         int i = 0;
         while( response == null || response.getHits().hits().length != 0){
         response = client.prepareSearch("twitter")
                .setTypes("users")
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("location", "value")))
                 .setSize(10)
                .setFrom(i * 10).execute().actionGet();
                 
          for(SearchHit hit : response.getHits()){
             esData.add(hit.getSource());
         }
         
         i++;
         }
return esData;

    }
    
    
    //fetching data from es with help of date.

    @GetMapping("/fetch/date/{value}")
    public List<Map<String, Object>> searchByDate(@PathVariable String value,String twitter, String users) throws TwitterException , IOException {
   
    int scrollSize = 10;
   
    List<Map<String,Object>> esData = new ArrayList<Map<String,Object>>();
   
    SearchResponse response = null;
         int i = 0;
         while( response == null || response.getHits().hits().length != 0){
         response = client.prepareSearch("twitter")
                .setTypes("users")
                .setQuery(QueryBuilders.matchPhraseQuery("date", "value"))
                 .setQuery(QueryBuilders.rangeQuery("date").gte("value").lte("value"))
                 .setSize(10)
                 .setFrom(i * 10).execute().actionGet();
                 
         for(SearchHit hit : response.getHits()){
             esData.add(hit.getSource());
         }
         
         i++;
         }
return esData;

    }
    
    //fetching data from es using location & date range
    
    @GetMapping("/fetch/location/date/{value}")
    public List<Map<String, Object>> searchByDate1(@PathVariable String value,String twitter, String users) throws TwitterException , IOException {
   
    int scrollSize = 10;
   
    List<Map<String,Object>> esData = new ArrayList<Map<String,Object>>();
   
    SearchResponse response = null;
         int i = 0;
         while( response == null || response.getHits().hits().length != 0){
         response = client.prepareSearch("twitter")
                .setTypes("users")
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("location", "value"))
                		                           .must(QueryBuilders.rangeQuery("date").gte("value").lte("value")))
                .setSize(10)
                .setFrom(i * 10).execute().actionGet();
                 
  
       
         for(SearchHit hit : response.getHits()){
             esData.add(hit.getSource());
         }
         
         i++;
         }
return esData;

    }
    
} 
    
    
    
    
    
    
    
   
    
    
    
    





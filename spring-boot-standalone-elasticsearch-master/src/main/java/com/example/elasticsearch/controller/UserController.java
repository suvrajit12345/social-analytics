package com.example.elasticsearch.controller;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;

import org.springframework.web.bind.MissingPathVariableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
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
import org.elasticsearch.search.SearchHits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.elasticsearch.model.User;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusUpdate;
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
   
 //saving  verizon data from twitter along with sentiment score in es
   
    @GetMapping("/save/verizon")
    @CrossOrigin(origins="http://localhost:3000")
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
    
    
    
//fetch all datas from es
    
    @GetMapping("/fetch/all")
    @CrossOrigin(origins="http://localhost:3000")
    public List<Map<String, Object>> searchByAll() throws TwitterException , IOException {
    	 
    	int scrollSize = 10;
    	
    	 List<Map<String,Object>> esData = new ArrayList<Map<String,Object>>();
    	
    	 SearchResponse response = null;
         int i = 0;
         while( response == null || response.getHits().hits().length != 0){
         response = client.prepareSearch("twitter")
                .setTypes("users")
                .setQuery(QueryBuilders.matchAllQuery())
                              
                .setSize(10)
                .setFrom(i * 10).execute().actionGet();
                 
          for(SearchHit hit : response.getHits()){
             esData.add(hit.getSource());
         }
          
         i++;
         }
return esData;

    }

    
    
  //fetching data from es with help of location
    
    @GetMapping("/fetch/location/{value}")
    @CrossOrigin(origins="http://localhost:3000")
    public List<Map<String, Object>> searchByLocationName(@PathVariable("value") String value) throws TwitterException , IOException {    	
    	int scrollSize = 10;
    	
    	 List<Map<String,Object>> esData = new ArrayList<Map<String,Object>>();
    	
    	 SearchResponse response = null;
         int i = 0;
         while( response == null || response.getHits().hits().length != 0){
         response = client.prepareSearch("twitter")
                .setTypes("users")

                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("location", value)))                

                              

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
    @CrossOrigin(origins="http://localhost:3000")
    public List<Map<String, Object>> searchByDate(@PathVariable("value") String value) throws TwitterException , IOException {
   
    int scrollSize = 10;
   
    List<Map<String,Object>> esData = new ArrayList<Map<String,Object>>();
   
    SearchResponse response = null;
         int i = 0;
         while( response == null || response.getHits().hits().length != 0){
         response = client.prepareSearch("twitter")
                .setTypes("users")
                .setQuery(QueryBuilders.matchPhraseQuery("date", value))
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
    
    @GetMapping("/fetch/location/date/{value}/{value1)/{value2}")
    @CrossOrigin(origins="http://localhost:3000")
    public  List<Map<String, Object>>  searchByDate1( @PathVariable("value") String value,@PathVariable("value1") String value1,@PathVariable("value2")String value2) throws TwitterException , IOException {
   
    int scrollSize = 10;
   
    List<Map<String,Object>> esData = new ArrayList<Map<String,Object>>();
   
    SearchResponse response = null;
         int i = 0;
         while( response == null || response.getHits().hits().length != 0){
         response = client.prepareSearch("twitter")
                .setTypes("users")
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("location", value))
                		                           .must(QueryBuilders.rangeQuery("date").gte(value1).lte(value2)))
                .setSize(10)
                .setFrom(i * 10).execute().actionGet();
                 
  
       
         for(SearchHit hit : response.getHits()){
             esData.add(hit.getSource());
         }
         
         i++;
         }
return esData;

    }
    
    
    
   
 //replying to a particular tweet//

    @GetMapping("/pull/twitter")
    public String pullTwitterData() throws TwitterException, IOException {
	
	
	ConfigurationBuilder cb = new ConfigurationBuilder();
	cb.setDebugEnabled(true)
	 .setOAuthConsumerKey("5WaGRlHTG3NS52Xg4Zg795PR6")
     .setOAuthConsumerSecret("hKZAHHmgzivBmMThNJWJTmZENPptexS5MagpFaJt57vZb2JaeY")
     .setOAuthAccessToken("1263643376055980041-nEfdv1uvMvfcakDAPkg3bVzGfusory")
     .setOAuthAccessTokenSecret("Gfdbj03FmhFasvmRLnmB7UimvWD9fPlpG2fdCUAwJts0I");
	        
	TwitterFactory tf = new TwitterFactory(cb.build());
	Twitter twitter = tf.getInstance();
	
	System.out.println("_________________________-----------------------------");
	Query query = new Query("@imSP_samal");
	query.setCount(1); // pull one record
    QueryResult result = twitter.search(query);
    for (Status status : result.getTweets()) {
        System.out.println("@" + status.getUser().getScreenName() + ":"+"Location-- :"+ status.getUser().getLocation()+"---" + status.getText());
        
        //reply to comment 
        Status status1 = twitter.showStatus( status.getId() );
        Status reply = twitter.updateStatus(new StatusUpdate(" @" + status1.getUser().getScreenName() + " "+ "Sorry for inconvenience caused to you. Our Verizon Team is looking into it. Please find the ticket No #2020058BAG").inReplyToStatusId(status1.getId()));
        
        
        // save data to ES
	    IndexResponse response = client.prepareIndex("users", "employee", Integer.toString( (int) status.getId() ))
                .setSource(jsonBuilder()
                        .startObject()
                        .field("date", status.getCreatedAt())
                        .field("name", status.getUser().getScreenName())
                        .field("location", status.getUser().getLocation())
                        .field("text", status.getText())
                        .field("score", SentimentAnalyzerService.analyse(status.getText()))
                        .endObject()
                )
                .get();
               System.out.println("response id:"+response.getId());
        
    }
    
   
	return  "Auto Reply Complated";
}

    
 // saving  AT&T data from twitter along with sentiment score in es  
 
    @GetMapping("/save/AT&T")
    @CrossOrigin(origins="http://localhost:3000")
  public  String SentimentAnalyzerService1()  throws TwitterException , IOException  {

   
   ConfigurationBuilder cb = new ConfigurationBuilder();
	cb.setDebugEnabled(true)
	.setOAuthConsumerKey("vHH0ybs1hrQBlVZNojhzkLoZC")
	.setOAuthConsumerSecret("lEHTyP1s1AQPqftt00D5rWq4yoriiWP02qOpKPgTbEGmiXrMkB")
	.setOAuthAccessToken("1263407896135643137-RmpJS84ZQqLqGRD9UdZocpUGu0902j") 
	.setOAuthAccessTokenSecret("HkDNxysJQHhqLqYPNObNsuvXTx3IG7kAmjkTY13JM24E7");
	TwitterFactory tf = new TwitterFactory(cb.build());
	Twitter twitter = tf.getInstance();
	
	Query query = new Query("#AT&T");
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
	 
	 IndexResponse response = client.prepareIndex("telecom", "network",Integer.toString((int) status.getId()))
                .setSource(jsonBuilder()
                        .startObject()
                        .field("SNumber", status.getId())
                        .field("hashtag", "#AT&T")
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

   
  //saving  TMobile data from twitter along with sentiment score in es  
  
    @GetMapping("/save/TMobile")
    @CrossOrigin(origins="http://localhost:3000")
   public  String SentimentAnalyzerService2()  throws TwitterException , IOException  {


   ConfigurationBuilder cb = new ConfigurationBuilder();
	cb.setDebugEnabled(true)
	.setOAuthConsumerKey("vHH0ybs1hrQBlVZNojhzkLoZC")
	.setOAuthConsumerSecret("lEHTyP1s1AQPqftt00D5rWq4yoriiWP02qOpKPgTbEGmiXrMkB")
	.setOAuthAccessToken("1263407896135643137-RmpJS84ZQqLqGRD9UdZocpUGu0902j") 
	.setOAuthAccessTokenSecret("HkDNxysJQHhqLqYPNObNsuvXTx3IG7kAmjkTY13JM24E7");
	TwitterFactory tf = new TwitterFactory(cb.build());
	Twitter twitter = tf.getInstance();
	
	Query query = new Query("#TMobile");
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
	 
	 IndexResponse response = client.prepareIndex("media","grid",Integer.toString((int) status.getId()))
             .setSource(jsonBuilder()
                     .startObject()
                     .field("SNumber", status.getId())
                     .field("hashtag", "#TMobile")
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
}


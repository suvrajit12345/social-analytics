package com.example.elasticsearch;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@SpringBootApplication
public class ElasticApplication {

	public static void main(String[] args) throws TwitterException {
		getTwitterinstance();
		SpringApplication.run(ElasticApplication.class, args);
	}
	
	
	public static void getTwitterinstance() throws TwitterException {
		/**
		 * if not using properties file, we can set access token by following way
		 */
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		        .setOAuthConsumerKey("5WaGRlHTG3NS52Xg4Zg795PR6")
		        .setOAuthConsumerSecret("hKZAHHmgzivBmMThNJWJTmZENPptexS5MagpFaJt57vZb2JaeY")
		        .setOAuthAccessToken("1263643376055980041-JCN238YfPffkNDN6auzcSO84NXPA6S")
		        .setOAuthAccessTokenSecret("JLXmklYSMEe8TzpEmpxCQssF1GcGQbKCD7NEpv5SXXZ7y");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();

		// Gets timeline.
//		Twitter twit = TwitterFactory.getSingleton();
//		List<Status> statuses = twitter.getHomeTimeline();
//		System.out.println("Showing home timeline.");
//		for (Status status : statuses) {
//		    System.out.println(status.getUser().getName() + ":" +
//		                       status.getText());
//		}
//		System.out.println("_________________________-----------------------------");
//		Query query = new Query("#verizon");
//		query.setCount(100);
//	    QueryResult result = twitter.search(query);
//	    for (Status status : result.getTweets()) {
//	        System.out.println("@" + status.getUser().getScreenName() + ":"+"Location-- :"+ status.getUser().getLocation()+"---" + status.getText());
//	    }
		

		
	}
}

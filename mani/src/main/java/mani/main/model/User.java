package mani.main.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mani.main.type.*;



public class User {
   
    private String name;
    private Date creationDate = new Date();
    private String location;
    private String text;
    private String SNumber;
    int sentimentType;
    int  santimentScore;
    
    
    
    
   
    public User(String name, Date creationDate, String location, String text, String sNumber, int sentimentType,int  santimentScore) {
		super();
		this.name = name;
		this.creationDate = creationDate;
		this.location = location;
		this.text = text;
		SNumber = sNumber;
		this.sentimentType = SentimentType.NEUTRAL.value;
		this.santimentScore =santimentScore;
		
	}

    public int getSantimentScore() {
        return santimentScore;
    }

    public void setSantimentScore(int santimentScore) {
        this.santimentScore =  santimentScore;
    }
   
    
    public String getSNumber() {
        return  SNumber;
    }

    public void setSNumber(String  SNumber) {
        this.SNumber =  SNumber;
    }
    
    
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    
   

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    public int getSentimentType() {
        return sentimentType;
    }

    public void setSentimentType(int sentimentType) {
        this.sentimentType = sentimentType;
    }

  
}




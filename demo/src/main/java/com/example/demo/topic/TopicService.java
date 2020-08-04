package com.example.demo.topic;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicService {
	
	@Autowired
	private TopicRepository topicRepository;
	
	public List<Topic> getAllTopics()
	{
		//return topics;
		List<Topic> topics=new ArrayList<>();
		topicRepository.findAll().forEach(topics::add);
		return topics;

	}
	
	public Optional<Topic> getTopic(String code)
	{
	 //return topics.stream().filter(t->t.getId().equals(id)).findFirst().get();
	 return	topicRepository.findById(code);
	}
	
	public void addTopic(Topic topic)
	{
		topicRepository.save(topic);
	}

	public void updateTopic(String code, Topic topic) {

		topicRepository.save(topic);
	}

	public void deleteTopic(String code) 
	{
		//topics.removeIf(t->t.getId().equals(id));
		topicRepository.deleteById(code);
	}
}

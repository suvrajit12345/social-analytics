package com.example.demo.topic;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TopicController {

	
	@Autowired
	private TopicService topicService;
	
	@RequestMapping("/topics")
	public List<Topic> getAllTopics()
	{
		return topicService.getAllTopics();
				
	}
	
	@RequestMapping("/topics/{code}")
	public Optional<Topic> getTopic(@PathVariable String code)
	{
		return topicService.getTopic(code);
	}
	
	@RequestMapping(method=RequestMethod.POST ,value="/topics")
	
		public void addTopic(@RequestBody Topic topic)
		{
			topicService.addTopic(topic);
		}
	

    @RequestMapping(method=RequestMethod.PUT ,value="/topics/{code}")
    public void updateTopic(@RequestBody Topic topic,@PathVariable String code)
    {
	topicService.updateTopic(code,topic);
    }

    @RequestMapping(method=RequestMethod.DELETE ,value="/topics/{code}")
    public void   deleteTopic(@PathVariable String code)
    {
    topicService.deleteTopic(code);
    }

   }


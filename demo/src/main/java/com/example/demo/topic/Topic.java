package com.example.demo.topic;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//import org.hibernate.annotations.GenericGenerator;

//import com.sun.istack.NotNull;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;


@Entity
@Table(name="CourseDetails")

public class Topic {

	//@Id
	//@GeneratedValue(generator="system-uuid")
	//@GenericGenerator(name="system-uuid", strategy = "uuid")
	//@NotNull
   // @Size(max = 20)
	
	@Id
	 @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	private String code;
	private String name;
	private String description;
	
	public Topic() {
		
	}
	
	public Topic(int id ,String code, String name, String description) {
		super();
		this.id=id;
		this.code = code;
		this.name = name;
		this.description = description;
	}
	
	public int getId() {
		  return id;
		 }
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}

package com.hackernews.assignment.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class User {

	private String id;
	private String about;
	private Long created;
	
	public User() {}
	
	public User(String id, String about, Long created) {
		super();
		this.id = id;
		this.about = about;
		this.created = created;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public Long getCreated() {
		return created;
	}
	public void setCreated(Long created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", about=" + about + ", created=" + created + "]";
	}
	
	
}

package com.hackernews.assignment.dao;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Story implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String title;
	private Integer score;
	private String url;
	private Long time;
	private List<Integer> kids;
	private String type;
	private String by;
	
	public Story() {	
		super();
	}

	public Story(Integer id, String title, Integer score, String url, Long time, List<Integer> kids,
			String type, String by) {
		super();
		this.id = id;
		this.title = title;
		this.score = score;
		this.url = url;
		this.time = time;
		this.kids = kids;
		this.type = type;
		this.by = by;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	@JsonIgnore
	public List<Integer> getKids() {
		return kids;
	}

	@JsonProperty
	public void setKids(List<Integer> kids) {
		this.kids = kids;
	}

	@JsonIgnore
	public String getType() {
		return type;
	}

	@JsonProperty
	public void setType(String type) {
		this.type = type;
	}

	public String getBy() {
		return by;
	}

	public void setBy(String by) {
		this.by = by;
	}

	@Override
	public String toString() {
		return "Story [id=" + id + ", title=" + title + ", score=" + score + ", url=" + url + ", time=" + time
				+ ", kids=" + kids + ", type=" + type + ", by=" + by + "]";
	}

}

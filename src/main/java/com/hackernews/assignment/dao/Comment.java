package com.hackernews.assignment.dao;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String text;
	private Integer parent;
	private Long time;
	private List<Integer> kids;
	private String type;
	private String by;
	
	private Integer calculatedDescandants;
	private Boolean isParentComment;
	private Integer userProfileAge;
	
	public Comment() {
		
	}

	public Integer getId() {
		return id;
	}

	@JsonProperty
	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@JsonIgnore
	public Integer getParent() {
		return parent;
	}

	@JsonProperty
	public void setParent(Integer parent) {
		this.parent = parent;
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

	@JsonIgnore
	public Boolean getIsParentComment() {
		return isParentComment;
	}

	@JsonProperty
	public void setIsParentComment(Boolean isParentComment) {
		this.isParentComment = isParentComment;
	}

	@JsonIgnore
	public Integer getCalculatedDescandants() {
		return calculatedDescandants;
	}

	@JsonProperty
	public void setCalculatedDescandants(Integer calculatedDescandants) {
		this.calculatedDescandants = calculatedDescandants;
	}

	public Integer getUserProfileAge() {
		return userProfileAge;
	}

	public void setUserProfileAge(Integer userProfileAge) {
		this.userProfileAge = userProfileAge;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", text=" + text + ", parent=" + parent + ", time=" + time + ", kids=" + kids
				+ ", type=" + type + ", by=" + by + ", calculatedDescandants=" + calculatedDescandants
				+ ", isParentComment=" + isParentComment + ", userProfileAge=" + userProfileAge + "]";
	}

	
}

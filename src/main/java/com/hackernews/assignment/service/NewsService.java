package com.hackernews.assignment.service;

import java.util.List;

import com.hackernews.assignment.dao.Comment;
import com.hackernews.assignment.dao.Story;

public interface NewsService {

	public List<Story> getTopStories();

	public List<Comment> getComments(String storyId);

	public List<Story> getPastStories();
	
}

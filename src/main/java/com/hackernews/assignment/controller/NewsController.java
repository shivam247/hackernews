package com.hackernews.assignment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.hackernews.assignment.dao.Comment;
import com.hackernews.assignment.dao.Story;
import com.hackernews.assignment.service.NewsService;

@RestController
public class NewsController {

	@Autowired
	private NewsService newsService;
	
	@GetMapping(path = "/top-stories", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Story>> getTopStories() {
		
		List<Story> stories = newsService.getTopStories();
		return new ResponseEntity<List<Story>>(stories, HttpStatus.OK);
	}
	
	@GetMapping(path = "/comments/{storyId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Comment>> getComment(@PathVariable String storyId) {
		
		List<Comment> stories = newsService.getComments(storyId);
		return new ResponseEntity<List<Comment>>(stories, HttpStatus.OK);
	}
	
	@GetMapping(path = "/past-stories", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Story>> getPastStories() {
		
		List<Story> stories = newsService.getPastStories();
		return new ResponseEntity<List<Story>>(stories, HttpStatus.OK);
	}
	
}

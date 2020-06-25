package com.hackernews.assignment.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hackernews.assignment.dao.Comment;
import com.hackernews.assignment.dao.Story;
import com.hackernews.assignment.dao.User;
import com.hackernews.assignment.utils.RestUtil;

@Service
public class NewsServiceImpl implements NewsService {

	private static Logger logger = LogManager.getLogger(NewsServiceImpl.class);

	@Autowired
	private RestUtil restUtil;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	@Qualifier(value = "taskExecutor")
	private TaskExecutor executor;

	@Value(value = "${hackernews.topstories.url}")
	private String topStoriesUrl;

	@Value(value = "${hackernews.item.url}")
	private String itemUrl;
	
	@Value(value = "${hackernews.user.url}")
	private String userUrl;
	
	/*
	 These results will include 'jobs' as well.
	 Because the hacker news API for /top-stories might include jobs.
	 Also, these might include dead stories.
	 Had raised a query with Shrenik whether to exclude these but did not got a response.
	 However, this could easily be handled if needed.
	 */
	@Override
	@Cacheable(cacheNames = "topstories", key = "'topstories'")
	public List<Story> getTopStories() {
		
		logger.debug("----Entered getTopStories()-------");
		logger.info("No records found in cache");
		
		//Get the list of all stories id
		List<Integer> storyIds = restUtil.exchangeAsList(topStoriesUrl, new ParameterizedTypeReference<List<Integer>>() {});
		logger.info("List of Stories obtained - {}", storyIds);
		
		//Fetching all the stories to get their scores 
		List<Story> storiesList = Collections.synchronizedList(new ArrayList<Story>());
		List<CompletableFuture<Void>> completableFutures = storyIds.stream()
				.map(storyId -> CompletableFuture.runAsync(() -> collectStory(storyId, storiesList), executor))
				.collect(Collectors.toList());
		
		CompletableFuture<Void> allStories = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[storyIds.size()]));
		try {
			//Wait for some time to finish all the tasks
			allStories.get(1, TimeUnit.MINUTES);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			logger.error("Exception occured while fetching stories", e);
		}
		
		//TODO: to decide whether to filter dead stories and jobs.
		//Sort on basis of score
		storiesList.sort(Comparator.comparing(Story::getScore).reversed());
		int size = storiesList.size();
		
		//Return top 10 stories
		List<Story> finalStories = new ArrayList<>(storiesList.subList(0, size>10 ? 10 : size));
		putToPastStoriesCache(finalStories);

		return finalStories;
	}
	
	
	@Async 
	private void putToPastStoriesCache(List<Story> stories) {
	  
		try{
			//This will be used to persist all story ids each time it is served.
			//Not storing data other than id because some field values might change later on.
			//During /past-stories we can return all stories except the latest one which can be seen at /top-stories.
			HashOperations<String, Long, List<Integer>> hashOperations = redisTemplate.opsForHash(); 
			long currentTimeMillis = Instant.now().toEpochMilli();
			List<Integer> ids = stories.stream().map(Story::getId).collect(Collectors.toList());
			hashOperations.put("allPastStories", currentTimeMillis, ids); 
			logger.info(String.format("Putting story ids to a different cache with key : %s, Value: %s", currentTimeMillis, ids));

		}catch (Exception e) {
			logger.warn("Unable to put top stories into cache", e);
		}
	}
	 

	@Override
	@Cacheable(cacheNames = "comments", key = "#storyId")  
	public List<Comment> getComments(String storyId) {

		logger.debug("----Entered getComments()-------");
		logger.info(String.format("No records found in cache for storyId : %s", storyId));
		List<Comment> comments = new ArrayList<>();

		String storyUrl = getItemUrl(storyId);
		Story story = restUtil.getFromUrl(storyUrl, Story.class);
		List<Integer> parentCommentIds = story.getKids();
		if(parentCommentIds == null)
			return comments;
					
		//Find descendants count of each child comment recursively
		Map<Integer, Comment> idToCommentMap = new ConcurrentHashMap<>();
		List<CompletableFuture<Integer>> descendantCountFutures = parentCommentIds.stream()
						.map(parentCommentId -> CompletableFuture.supplyAsync(() -> getDescendantsCount(parentCommentId, idToCommentMap, true), executor)
			 			.thenCompose(Function.identity()))
						.collect(Collectors.toList());
		
		//Wait for all the threads to finish
		CompletableFuture.allOf(descendantCountFutures.toArray(new CompletableFuture[descendantCountFutures.size()])).join();
		
		//Filter and sort parent comments based on their descendants count
		List<Comment> allComments = new ArrayList<>(idToCommentMap.values());
		comments = allComments.stream().filter(Comment::getIsParentComment)
						.sorted(Comparator.comparing(Comment::getCalculatedDescandants).reversed())
						.collect(Collectors.toList());
		
		//Get the user profile age
		List<CompletableFuture<Void>> getUserAgeFutures = comments.stream()
				.map(comment -> CompletableFuture.runAsync(() -> fetchUserProfileAge(comment), executor))
				.collect(Collectors.toList());
		
		try {
			//Wait for some time to finish all the tasks
			CompletableFuture.allOf(getUserAgeFutures.toArray(new CompletableFuture[getUserAgeFutures.size()])).get(10, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			logger.error("Exception occured while fetching stories", e);
		}
		
		logger.info("All Parent Comments : {}", comments);
		int size = comments.size();
		
		//Return top 20 comments
		return new ArrayList<>(comments.subList(0, size>20 ? 20 : size));
	}

	private void fetchUserProfileAge(Comment comment) {
		
		String userId = comment.getBy();
		if(userId == null) {
			return;
		}
		
		String url = getFormedUrl(userUrl, comment.getBy());
		User user = restUtil.getFromUrl(url, User.class);
		if(user == null) {
			return;
		}
		
		Long creationTimeEpoch = user.getCreated();
		if(creationTimeEpoch == null) {
			return;
		}
		
		LocalDate endDate = LocalDate.now(ZoneId.systemDefault());
		LocalDate startDate = Instant.ofEpochSecond(creationTimeEpoch).atZone(ZoneId.systemDefault()).toLocalDate();
		
		Period period = Period.between(startDate, endDate);
		comment.setUserProfileAge(period.getYears());
	}

	private CompletableFuture<Integer> getDescendantsCount(Integer commentId, Map<Integer, Comment> commentMap, boolean isParentComment) {
		
		Comment comment = restUtil.getFromUrl(getItemUrl(String.valueOf(commentId)), Comment.class);
		comment.setIsParentComment(isParentComment);
		commentMap.put(commentId, comment);
		
		List<Integer> subCommentIds = comment.getKids();
		if(subCommentIds == null || subCommentIds.size() == 0) {
			comment.setCalculatedDescandants(0);
			return CompletableFuture.completedFuture(0);
		}
		else {
			int noOfChilds = subCommentIds.size();
			List<CompletableFuture<Integer>> completableFutures = subCommentIds.stream()
							.map(subCommentId -> CompletableFuture.supplyAsync(() -> getDescendantsCount(subCommentId, commentMap, false), executor)
				 			.thenCompose(Function.identity()))
							.collect(Collectors.toList());
			int totalDescendants = completableFutures.stream().mapToInt(x -> x.join().intValue()).sum() + noOfChilds;
			comment.setCalculatedDescandants(totalDescendants);
			return CompletableFuture.completedFuture(totalDescendants);
		}
	}
	
	/*
	  Not sure whether this was the use case for /past-stories.
	  Had dropped a query mail for the same to Shrenik, but did not got back a response.
	  Basically, this would return all the top stories that were served previously till now excluding the current top 10.
	  Also, the stories will have the latest scores and other values because only their ids were stored previously.
	  Of course, these values will again be cached for 10 minutes.
	 */
	@Override
	@Cacheable(cacheNames = "pastStories", key = "'pastStories'")
	public List<Story> getPastStories() {
		
		logger.info("No records found in cache");
		logger.debug("-----Entered getPastStories()---------");
		Map<Long, List<Integer>> entries = null;
		try {
			HashOperations<String, Long, List<Integer>> hashOperations = redisTemplate.opsForHash();
			entries = hashOperations.entries("allPastStories");
		} catch (Exception e) {
			logger.error(String.format("Unable to fetch records from Cache %s", "allPastStories"), e);
			throw e;
		}
		
		//Fetch all the stories that were served using /top-stories till now.
		TreeMap<Long, List<Integer>> sortedMap = new TreeMap<>(Collections.reverseOrder());
		sortedMap.putAll(entries);
		logger.info("Entries: {}", sortedMap);
		Long firstEntry = sortedMap.firstEntry().getKey();
		
		//Remove the latest stories which can anyways be seen through /top-stories
		List<Integer> storyIds = sortedMap.entrySet().stream()
					.filter(entry -> !entry.getKey().equals(firstEntry))
					.flatMap(x -> x.getValue().stream())
					.distinct()
					.collect(Collectors.toList());
		
		//Get the responses of Each story again because scores and other fields might change
		List<Story> pastStoriesList = Collections.synchronizedList(new ArrayList<Story>());
		List<CompletableFuture<Void>> completableFutures = storyIds.stream()
				.map(storyId -> CompletableFuture.runAsync(() -> collectStory(storyId, pastStoriesList), executor))
				.collect(Collectors.toList());
		
		CompletableFuture<Void> allStories = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[storyIds.size()]));
		try {
			//Wait for some time to finish all the tasks
			allStories.get(1, TimeUnit.MINUTES);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			logger.error("Exception occured while fetching stories", e);
		}
		
		return pastStoriesList;
	}
	
	public void collectStory(Integer id, List<Story> collectorList) {
		
		String storyId = String.valueOf(id);
		logger.info(String.format("Fetching results for storyId - %s", storyId));
		String url = getItemUrl(storyId);
		Story story = restUtil.getFromUrl(url, Story.class);
		if(story == null) {
			logger.warn(String.format("No story available for id : %s", storyId));
			return;
		} 
		collectorList.add(story);
	}
	
	private String getItemUrl(String itemId) {
		return getFormedUrl(itemUrl, itemId);
	}
	
	private static String getFormedUrl(String baseUrl, String id) {
		return baseUrl.concat(id).concat(".json");
	}



}

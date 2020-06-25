package com.hackernews.assignment.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestUtil {

	private static Logger logger = LogManager.getLogger(RestUtil.class);
	
	@Value("${timeoutSec:10}")
	private long timeoutSec;
	
	@Autowired
	RestTemplate restTemplate;

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder
		           .setConnectTimeout(Duration.ofSeconds(timeoutSec))
		           .setReadTimeout(Duration.ofSeconds(timeoutSec))
		           .build();
	}
	
	public <T> List<T> exchangeAsList(String url, ParameterizedTypeReference<List<T>> responseType) {
		
		ResponseEntity<List<T>> responseEntity = null;
		logger.debug("Url getting hit is : {}", url);
		try {
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
		}catch (Exception e) {
			logger.error(String.format("An exception occured while trying to get response from url : %s", url), e);
		}
		
	    if(responseEntity != null) {
	    	List<T> list = responseEntity.getBody();
	    	if(list != null) {
	    		logger.debug("Response is : {}", list);
	    		return list;
	    	}
	    }
	    return new ArrayList<T>();
	}
	
	public <T> T getFromUrl(String url, Class<T> responseType) {
		
		ResponseEntity<T> responseEntity = null;
		logger.info("Url getting hit is : {}", url);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
		}catch (Exception e) {
			logger.error(String.format("An exception occured while trying to get response from url : %s", url), e);
		}
		
		if(responseEntity != null) {
			logger.info("Response is : {}", responseEntity.getBody());
			return responseEntity.getBody();
		}
		return null;
		
	}

}

package com.hackernews.assignment.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.hackernews.assignment.exception.InternalProcessingException;

@Component
@Aspect
public class RestControllerAspect {

	private static final Logger logger = LogManager.getLogger(RestControllerAspect.class);

	@Pointcut("execution(* com.hackernews.assignment.controller.NewsController.*(..))")
	public void controllerPointcut() {};

	@SuppressWarnings("unchecked")
	@Around("controllerPointcut()")
	public ResponseEntity<String> aroundRestCall(ProceedingJoinPoint jp) throws Throwable{
		try {
			long start = System.currentTimeMillis();

			Object[] args = jp.getArgs();
			Object output =  (ResponseEntity<String>) jp.proceed(args);
			long elapsedTime = System.currentTimeMillis() - start;
			logger.info(String.format(String.format("%s responseTime = %s ms",jp.getSignature().getName(), elapsedTime)));
			return (ResponseEntity<String>) output;
		}
		catch (Exception x) {
			logger.error("We have an exception",x );
			return new ResponseEntity<String>((new InternalProcessingException(x)).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}

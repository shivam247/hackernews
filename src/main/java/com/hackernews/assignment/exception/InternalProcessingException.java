package com.hackernews.assignment.exception;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("serial")
public class InternalProcessingException extends Exception {
	public InternalProcessingException(Throwable cause) {
		super(cause);
	}
	
	public String toString() {
		return toString("An unexpected error occured", "500");
	}
	
	public String toString(String errorMessage, String errorCode) {
		return new Message(errorMessage, errorCode).toString();
	}
	
	public static class Message {
		
		private String errorMessage;
		private String errorCode;
		
		public Message(String errorMessage, String errorCode) {
			super();
			this.errorMessage = errorMessage;
			this.errorCode = errorCode;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public String getErrorCode() {
			return errorCode;
		}

		@Override
		public String toString() {
			ObjectMapper Obj = new ObjectMapper(); 
			  
	        try { 
	            String jsonStr = Obj.writeValueAsString(this);
	            return jsonStr;
	        }catch (Exception e) {
	        	return "";
	        }
		}
		
	}
}


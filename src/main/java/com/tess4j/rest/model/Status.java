package com.tess4j.rest.model;

public class Status {
	/**
	 * The status message
	 */
	private String message;

	public Status(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}


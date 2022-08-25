package com.coderlook.chatrachatri.subscriptions.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1728952325890679440L;
	
	private String status;
	private String message;
	private String details;

}

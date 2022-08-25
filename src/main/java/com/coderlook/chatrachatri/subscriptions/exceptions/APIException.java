package com.coderlook.chatrachatri.subscriptions.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class APIException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6036887478078523155L;
	
	private String status;
	private String message;
}

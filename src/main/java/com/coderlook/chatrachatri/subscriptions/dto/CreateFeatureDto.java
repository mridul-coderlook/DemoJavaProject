package com.coderlook.chatrachatri.subscriptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFeatureDto {

	private String name;
	private String displayText;
	private Boolean markEnabled;
	private Boolean showInDisplay;
	private Boolean required;
    
}

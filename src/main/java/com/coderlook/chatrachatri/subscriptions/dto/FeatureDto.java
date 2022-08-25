package com.coderlook.chatrachatri.subscriptions.dto;

import com.coderlook.chatrachatri.subscriptions.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureDto {

    private Long id;
	private String name;
	private String displayText;
	private Boolean markEnabled;
	private Boolean showInDisplay;
	private Boolean required;
	private Status status;
    
}

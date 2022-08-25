package com.coderlook.chatrachatri.subscriptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePlanConfigDto {
	
	private Long planId;
	private Long featureId;
	private Integer count;
	private Integer displayOrder;

}

package com.coderlook.chatrachatri.subscriptions.dto;

import com.coderlook.chatrachatri.subscriptions.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanConfigDto {
	
	private Long id;
	private PlanDto plan;
	private FeatureDto feature;
	private Integer count;
	private Integer displayOrder;
	private Status status;

}

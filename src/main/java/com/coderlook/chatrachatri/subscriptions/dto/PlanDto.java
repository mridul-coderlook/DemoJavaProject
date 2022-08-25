package com.coderlook.chatrachatri.subscriptions.dto;

import com.coderlook.chatrachatri.subscriptions.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanDto {
	
	private Long id;
	private String name;
	private String code;
	private String description;
	private Double price;
	private Boolean free;
	private Integer weight;
	private String frequency;
	private Integer duration;
	private Status status;

}

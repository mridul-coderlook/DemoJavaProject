package com.coderlook.chatrachatri.subscriptions.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDto {
	
	private Long planId;
	private LocalDate orderDate;
	private Integer noOfUser;
	private Integer duration;
	private Double price;
	private Boolean upgrade;
	private Double discount;
	private String couponCode;
	private Double discountPercentage;
	private Double unitPrice;

}

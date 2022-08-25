package com.coderlook.chatrachatri.subscriptions.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentHistoryDto {

	private String transaction;
	private Long planId;
	private Long orderRequestId;
	private LocalDate paymentDate;
	private Double amount;
	private String paymentType;
	private String paymentStatus;

}

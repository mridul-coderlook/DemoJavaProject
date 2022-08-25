package com.coderlook.chatrachatri.subscriptions.dto;

import java.time.LocalDate;

import com.coderlook.chatrachatri.subscriptions.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryDto {

	private Long id;
	private String transaction;
	private PlanDto plan;
	private OrderRequestDto orderRequest;
	private LocalDate paymentDate;
	private Double amount;
	private String paymentType;
	private String paymentStatus;
	private Status status;

}

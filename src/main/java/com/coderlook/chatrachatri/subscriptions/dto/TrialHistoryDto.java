package com.coderlook.chatrachatri.subscriptions.dto;

import com.coderlook.chatrachatri.subscriptions.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrialHistoryDto {

	private Long id;
	private Long institutionId;
	private PlanDto plan;
	private SubscriptionHistoryDto subscriptionHistory;
	private Status status;

}

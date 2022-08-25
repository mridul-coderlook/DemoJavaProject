package com.coderlook.chatrachatri.subscriptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTrialHistoryDto {

	private Long institutionId;
	private Long planId;
	private Long subscriptionHistoryId;

}

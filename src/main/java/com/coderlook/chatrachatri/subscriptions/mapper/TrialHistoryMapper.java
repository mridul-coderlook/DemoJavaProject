package com.coderlook.chatrachatri.subscriptions.mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_NULL;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.coderlook.chatrachatri.subscriptions.dto.CreateTrialHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.TrialHistoryDto;
import com.coderlook.chatrachatri.subscriptions.entity.TrialHistory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE, nullValueMappingStrategy = RETURN_NULL)
public interface TrialHistoryMapper {

	@Mappings({
		@Mapping(target = "subscriptionHistory.plan", 							ignore = true),
		@Mapping(target = "subscriptionHistory.orderRequest.plan", 				ignore = true),
		@Mapping(target = "subscriptionHistory.subscription.plan", 				ignore = true),
		@Mapping(target = "subscriptionHistory.subscription.orderRequest", 		ignore = true),
	})
	TrialHistoryDto trialHistoryToTrialHistoryDto(TrialHistory trialHistory);

	@Mappings({
		@Mapping(target = "plan.id", 											source = "trialHistoryDto.plan.id"),
		@Mapping(target = "subscriptionHistory.id", 							source = "trialHistoryDto.subscriptionHistory.id"),
	})
	TrialHistory trialHistoryDtoToTrialHistory(TrialHistoryDto trialHistoryDto);

	@Mappings({
			@Mapping(target = "plan.id", 										source = "createTrialHistoryDto.planId"),
			@Mapping(target = "subscriptionHistory.id", 						source = "createTrialHistoryDto.subscriptionHistoryId"),
	})
	TrialHistory createTrialHistoryDtoToTrialHistory(CreateTrialHistoryDto createTrialHistoryDto);

}

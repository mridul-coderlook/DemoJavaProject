package com.coderlook.chatrachatri.subscriptions.mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_NULL;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.coderlook.chatrachatri.subscriptions.dto.CreateSubscriptionHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.SubscriptionHistoryDto;
import com.coderlook.chatrachatri.subscriptions.entity.SubscriptionHistory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE, nullValueMappingStrategy = RETURN_NULL)
public interface SubscriptionHistoryMapper {

	@Mappings({
		@Mapping(target = "orderRequest.plan", 				ignore = true),
		@Mapping(target = "subscription.plan", 				ignore = true),
		@Mapping(target = "subscription.orderRequest", 		ignore = true),
	})
	SubscriptionHistoryDto subscriptionHistoryToSubscriptionHistoryDto(SubscriptionHistory subscriptionHistory);

	@Mappings({
		@Mapping(target = "plan.id", 				source = "subscriptionHistoryDto.plan.id"),
		@Mapping(target = "orderRequest.id", 		source = "subscriptionHistoryDto.orderRequest.id"),
		@Mapping(target = "subscription.id", 		source = "subscriptionHistoryDto.subscription.id"),
	})
	SubscriptionHistory subscriptionHistoryDtoToSubscriptionHistory(SubscriptionHistoryDto subscriptionHistoryDto);

	@Mappings({
			@Mapping(target = "plan.id", 			source = "createSubscriptionHistoryDto.planId"),
			@Mapping(target = "orderRequest.id", 	source = "createSubscriptionHistoryDto.orderRequestId"),
			@Mapping(target = "subscription.id", 	source = "createSubscriptionHistoryDto.subscriptionId"),
	})
	SubscriptionHistory createSubscriptionHistoryDtoToSubscriptionHistory(CreateSubscriptionHistoryDto createSubscriptionHistoryDto);

}

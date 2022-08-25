package com.coderlook.chatrachatri.subscriptions.mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_NULL;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.coderlook.chatrachatri.subscriptions.dto.CreateSubscriptionDto;
import com.coderlook.chatrachatri.subscriptions.dto.SubscriptionDto;
import com.coderlook.chatrachatri.subscriptions.entity.Subscription;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE, nullValueMappingStrategy = RETURN_NULL)
public interface SubscriptionMapper {

	@Mappings({
		@Mapping(target = "orderRequest.plan", 		ignore = true),
	})
	SubscriptionDto subscriptionToSubscriptionDto(Subscription subscription);

	@Mappings({
		@Mapping(target = "plan.id", 				source = "subscriptionDto.plan.id"),
		@Mapping(target = "orderRequest.id", 		source = "subscriptionDto.orderRequest.id"),
	})
	Subscription subscriptionDtoToSubscription(SubscriptionDto subscriptionDto);

	@Mappings({
			@Mapping(target = "plan.id", 			source = "createSubscriptionDto.planId"),
			@Mapping(target = "orderRequest.id", 	source = "createSubscriptionDto.orderRequestId"),
	})
	Subscription createSubscriptionDtoToSubscription(CreateSubscriptionDto createSubscriptionDto);

}

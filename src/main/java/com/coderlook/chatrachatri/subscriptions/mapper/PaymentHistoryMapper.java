package com.coderlook.chatrachatri.subscriptions.mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_NULL;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.coderlook.chatrachatri.subscriptions.dto.CreatePaymentHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.PaymentHistoryDto;
import com.coderlook.chatrachatri.subscriptions.entity.PaymentHistory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE, nullValueMappingStrategy = RETURN_NULL)
public interface PaymentHistoryMapper {

	@Mappings({
		@Mapping(target = "orderRequest.plan", 			ignore = true),
	})
	PaymentHistoryDto paymentHistoryToPaymentHistoryDto(PaymentHistory paymentHistory);

	@Mappings({
		@Mapping(target = "plan.id", 					source = "paymentHistoryDto.plan.id"),
		@Mapping(target = "orderRequest.id", 			source = "paymentHistoryDto.orderRequest.id"),
	})
	PaymentHistory paymentHistoryDtoToPaymentHistory(PaymentHistoryDto paymentHistoryDto);

	@Mappings({
			@Mapping(target = "plan.id", 				source = "createPaymentHistoryDto.planId"),
			@Mapping(target = "orderRequest.id", 		source = "createPaymentHistoryDto.orderRequestId"),
	})
	PaymentHistory createPaymentHistoryDtoToPaymentHistory(CreatePaymentHistoryDto createPaymentHistoryDto);

}

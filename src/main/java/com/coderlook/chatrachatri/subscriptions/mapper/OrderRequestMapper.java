package com.coderlook.chatrachatri.subscriptions.mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_NULL;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.coderlook.chatrachatri.subscriptions.dto.CreateOrderRequestDto;
import com.coderlook.chatrachatri.subscriptions.dto.OrderRequestDto;
import com.coderlook.chatrachatri.subscriptions.entity.OrderRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE, nullValueMappingStrategy = RETURN_NULL)
public interface OrderRequestMapper {

	OrderRequestDto orderRequestToOrderRequestDto(OrderRequest orderRequest);

	@Mappings({
		@Mapping(target = "plan.id", 		source = "orderRequestDto.plan.id"),
	})
	OrderRequest orderRequestDtoToOrderRequest(OrderRequestDto orderRequestDto);

	@Mappings({
			@Mapping(target = "plan.id", 		source = "createOrderRequestDto.planId"),
	})
	OrderRequest createOrderRequestDtoToOrderRequest(CreateOrderRequestDto createOrderRequestDto);

}

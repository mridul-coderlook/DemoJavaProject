package com.coderlook.chatrachatri.subscriptions.mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_NULL;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;

import com.coderlook.chatrachatri.subscriptions.dto.CreatePlanDto;
import com.coderlook.chatrachatri.subscriptions.dto.PlanDto;
import com.coderlook.chatrachatri.subscriptions.entity.Plan;


@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE, nullValueMappingStrategy = RETURN_NULL)
public interface PlanMapper {
	
	PlanDto planToPlanDto(Plan plan);
	
	Plan planDtoToPlan(PlanDto planDto);
	
	Plan createPlanDtoToPlan(CreatePlanDto createPlanDto);
	
}

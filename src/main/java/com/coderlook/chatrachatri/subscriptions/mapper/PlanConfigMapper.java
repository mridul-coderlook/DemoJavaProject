package com.coderlook.chatrachatri.subscriptions.mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_NULL;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.coderlook.chatrachatri.subscriptions.dto.CreatePlanConfigDto;
import com.coderlook.chatrachatri.subscriptions.dto.PlanConfigDto;
import com.coderlook.chatrachatri.subscriptions.entity.PlanConfig;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE, nullValueMappingStrategy = RETURN_NULL)
public interface PlanConfigMapper {

	PlanConfigDto planConfigToPlanConfigDto(PlanConfig planConfig);

	@Mappings({
		@Mapping(target = "plan.id", 		source = "planConfigDto.plan.id"),
		@Mapping(target = "feature.id", 	source = "planConfigDto.feature.id"),
	})
	PlanConfig planConfigDtoToPlanConfig(PlanConfigDto planConfigDto);

	@Mappings({
			@Mapping(target = "plan.id", 		source = "createPlanConfigDto.planId"),
			@Mapping(target = "feature.id", 	source = "createPlanConfigDto.featureId"),
	})
	PlanConfig createPlanConfigDtoToPlanConfig(CreatePlanConfigDto createPlanConfigDto);

}

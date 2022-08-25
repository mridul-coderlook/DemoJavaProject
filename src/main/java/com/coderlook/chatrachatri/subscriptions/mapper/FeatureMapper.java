package com.coderlook.chatrachatri.subscriptions.mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_NULL;
import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;

import com.coderlook.chatrachatri.subscriptions.dto.CreateFeatureDto;
import com.coderlook.chatrachatri.subscriptions.dto.FeatureDto;
import com.coderlook.chatrachatri.subscriptions.entity.Feature;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE, nullValueMappingStrategy = RETURN_NULL)
public interface FeatureMapper {

	FeatureDto featureToFeatureDto(Feature feature);

	Feature featureDtoToFeature(FeatureDto featureDto);

	Feature createFeatureDtoToFeature(CreateFeatureDto createFeatureDto);

}

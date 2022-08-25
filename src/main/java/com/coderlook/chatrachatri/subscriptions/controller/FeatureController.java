package com.coderlook.chatrachatri.subscriptions.controller;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coderlook.chatrachatri.subscriptions.dto.CreateFeatureDto;
import com.coderlook.chatrachatri.subscriptions.dto.FeatureDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.Feature;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.FeatureMapper;
import com.coderlook.chatrachatri.subscriptions.service.FeatureService;

@RestController
@RequestMapping("/api/feature")
public class FeatureController {
	@Autowired
	private FeatureService featureService;

	@Autowired
	private FeatureMapper featureMapper;

	private static final Logger logger = LoggerFactory.getLogger(FeatureController.class);

	@GetMapping("/{id}")
	public ResponseEntity<?> getFeature(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling getFeature by feature_id {}", id);
		try {
			FeatureDto featureDto = this.featureMapper
					.featureToFeatureDto(this.featureService.getFeatureById(id));
			logger.info("Completed - feature details found {} while calling getFeature by id {}",
					featureDto, id);
			return new ResponseEntity<FeatureDto>(featureDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of Feature id {} with BusinessException {} ", id, be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to get details of Feature id {} with Exception {} ", id, e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> getSearchFeatures(
			@RequestParam(value = "name", required = false) Optional<String> name,
			@RequestParam(value = "displayText", required = false) Optional<String> displayText,
			@RequestParam(value = "markEnabled", required = false) Optional<Boolean> markEnabled,
			@RequestParam(value = "showInDisplay", required = false) Optional<Boolean> showInDisplay,
			@RequestParam(value = "status", required = false) Optional<Status> status,
			@RequestParam(value = "pageNumber", required = false) Optional<Integer> pageNumber,
			@RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize,
			@RequestParam(value = "sort", required = false) Optional<String> sort) {
		logger.info("Started - Calling getSearchFeatures");
		try {
			Page<Feature> result = this.featureService.search(name, displayText, markEnabled, showInDisplay, status,
					pageNumber, pageSize, sort);
			Page<FeatureDto> featurePageDto = result
					.map(Feature -> this.featureMapper.featureToFeatureDto(Feature));
			logger.info("Completed - Record found {} while calling getSearchFeatures", result.getTotalElements());
			return new ResponseEntity<Page<FeatureDto>>(featurePageDto, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Unable to fetch list of Feature with Exception {} ", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/create")
	public ResponseEntity<?> create(
			@NotNull @Valid @RequestBody CreateFeatureDto createFeatureDto) {
		logger.info("Started - Calling create Feature details {}", createFeatureDto);
		try {
			SuccessDto successDto = this.featureService
					.createFeature(
							this.featureMapper.createFeatureDtoToFeature(createFeatureDto));
			logger.info("Completed - Calling create Feature details {} with id {} ",
					createFeatureDto, successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.CREATED);
		} catch (BusinessException be) {
			logger.error("Unable to create Feature {} with BusinessException {} ",
					createFeatureDto, be.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIException(
					String.valueOf(HttpStatus.CONFLICT),
					ExceptionMessageConstants.FAILED_TO_CREATE_FEATURE));
		} catch (Exception e) {
			logger.error("Unable to create Feature {} with Exception {}",
					createFeatureDto, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
					ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/update")
	public ResponseEntity<?> update(@NotNull @Valid @RequestBody FeatureDto featureDto) {
		logger.info("Started - Calling update feature details {}", featureDto);
		try {
			SuccessDto successDto = this.featureService
					.updateFeature(this.featureMapper.featureDtoToFeature(featureDto));
			logger.info("Completed - Calling update Feature by feature details {} with id	{}",
					featureDto, successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of Feature id {} with BusinessException	{} ",
					featureDto.getId(), be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIException(
						HttpStatus.BAD_REQUEST.toString(),
						ExceptionMessageConstants.FAILED_TO_UPDATE_FEATURE));

			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED));

			} else {

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(),
						ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}

		} catch (Exception e) {
			logger.error("Unable to get details of Feature id {} with Exception {} ",
					featureDto.getId(), e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(),
					ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/delete/{id}")
	public ResponseEntity<?> delete(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling delete feature details by id {}", id);
		try {
			SuccessDto successDto = this.featureService.deleteFeature(id);
			logger.info("Completed - Calling delete Feature by id {}",
					successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to delete feature id {} with BusinessException {} ", id,
					be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new APIException(HttpStatus.BAD_REQUEST.toString(),
								ExceptionMessageConstants.FAILED_TO_DELETE_FEATURE));
			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new APIException(HttpStatus.NOT_FOUND.toString(),
								ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(),
						ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to delete Feature id {} with Exception {}", id,
					e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(),
					ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

}

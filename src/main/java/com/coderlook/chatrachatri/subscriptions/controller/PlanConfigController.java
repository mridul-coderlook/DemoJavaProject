package com.coderlook.chatrachatri.subscriptions.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coderlook.chatrachatri.subscriptions.dto.CreatePlanConfigDto;
import com.coderlook.chatrachatri.subscriptions.dto.PlanConfigDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.PlanConfigMapper;
import com.coderlook.chatrachatri.subscriptions.service.PlanConfigService;

@RestController
@RequestMapping("/api/plan-config")
public class PlanConfigController {
	@Autowired
	private PlanConfigService planConfigService;

	@Autowired
	private PlanConfigMapper planConfigMapper;

	private static final Logger logger = LoggerFactory.getLogger(PlanConfigController.class);

	@GetMapping("/{id}")
	public ResponseEntity<?> getPlanConfig(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling getPlanConfig by PlanConfig id {}", id);
		try {
			PlanConfigDto planConfigDto = this.planConfigMapper.planConfigToPlanConfigDto(this.planConfigService.getPlanConfigById(id));
			logger.info("Completed - PlanConfig details found {} while calling getPlanConfig by id {}", planConfigDto, id);
			return new ResponseEntity<PlanConfigDto>(planConfigDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of PlanConfig id {} with BusinessException {} ", id, be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(), ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to get details of PlanConfig id {} with Exception {} ", id, e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/create")
	public ResponseEntity<?> create(
			@NotNull @Valid @RequestBody CreatePlanConfigDto createPlanConfigDto) {
		logger.info("Started - Calling create PlanConfig details {}", createPlanConfigDto);
		try {
			SuccessDto successDto = this.planConfigService
					.createPlanConfig(this.planConfigMapper.createPlanConfigDtoToPlanConfig(createPlanConfigDto));
			logger.info("Completed - Calling create PlanConfig details {} with id {} ", createPlanConfigDto,
					successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.CREATED);
		} catch (BusinessException be) {
			logger.error("Unable to create PlanConfig {} with BusinessException {} ", createPlanConfigDto, be.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIException(
					String.valueOf(HttpStatus.CONFLICT), ExceptionMessageConstants.FAILED_TO_CREATE_PLAN_CONFIG));
		} catch (Exception e) {
			logger.error("Unable to create PlanConfig {} with Exception {}", createPlanConfigDto, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/update")
	public ResponseEntity<?> update(@NotNull @Valid @RequestBody PlanConfigDto planConfigDto) {
		logger.info("Started - Calling update PlanConfig details {}", planConfigDto);
		try {
			SuccessDto successDto = this.planConfigService.updatePlanConfig(this.planConfigMapper.planConfigDtoToPlanConfig(planConfigDto));
			logger.info("Completed - Calling update PlanConfig by PlanConfig details {} with id {}",
					planConfigDto, successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of PlanConfig id {} with BusinessException {} ", planConfigDto.getId(),
					be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIException(
						HttpStatus.BAD_REQUEST.toString(), ExceptionMessageConstants.FAILED_TO_UPDATE_PLAN_CONFIG));

			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(), ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED));

			} else {

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}

		} catch (Exception e) {
			logger.error("Unable to get details of PlanConfig id {} with Exception {} ", planConfigDto.getId(), e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/delete/{id}")
	public ResponseEntity<?> delete(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling delete PlanConfig details by id {}", id);
		try {
			SuccessDto successDto = this.planConfigService.deletePlanConfig(id);
			logger.info("Completed - Calling delete PlanConfig by id {}", successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to delete PlanConfig id {} with BusinessException {} ", id, be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new APIException(HttpStatus.BAD_REQUEST.toString(),
								ExceptionMessageConstants.FAILED_TO_DELETE_PLAN_CONFIG));
			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new APIException(HttpStatus.NOT_FOUND.toString(),
								ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to delete PlanConfig id {} with Exception {}", id, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

}

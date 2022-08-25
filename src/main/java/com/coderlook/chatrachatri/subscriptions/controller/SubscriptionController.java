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

import com.coderlook.chatrachatri.subscriptions.dto.CreateSubscriptionDto;
import com.coderlook.chatrachatri.subscriptions.dto.SubscriptionDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.entity.Subscription;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.SubscriptionMapper;
import com.coderlook.chatrachatri.subscriptions.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private SubscriptionMapper subscriptionMapper;

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

	@GetMapping("/{id}")
	public ResponseEntity<?> getSubscription(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling getSubscription by Subscription id {}", id);
		try {
			SubscriptionDto subscriptionDto = this.subscriptionMapper
					.subscriptionToSubscriptionDto(this.subscriptionService.getSubscriptionById(id));
			logger.info("Completed - Subscription details found {} while calling getSubscription by id {}",
					subscriptionDto, id);
			return new ResponseEntity<SubscriptionDto>(subscriptionDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of Subscription id {} with BusinessException {} ", id, be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to get details of Subscription id {} with Exception {} ", id, e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> getAllSubscriptions(
			@RequestParam(value = "plan", required = false) Optional<String> planName,
			@RequestParam(value = "status", required = false) Optional<Status> status,
			@RequestParam(value = "pageNumber", required = false) Optional<Integer> pageNumber,
			@RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize,
			@RequestParam(value = "sort", required = false) Optional<String> sort) {
		logger.info("Started - Calling getAllSubscriptions");
		try {
			Page<Subscription> result = this.subscriptionService.search(planName, status, pageNumber,
					pageSize, sort);
			Page<SubscriptionDto> subscriptionPageDto = result
					.map(subscription -> this.subscriptionMapper.subscriptionToSubscriptionDto(subscription));
			logger.info("Completed - Record found {} while calling getAllSubscriptions", result.getTotalElements());
			return new ResponseEntity<Page<SubscriptionDto>>(subscriptionPageDto, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Unable to fetch list of Subscription with Exception {} ", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/create")
	public ResponseEntity<?> create(
			@NotNull @Valid @RequestBody CreateSubscriptionDto createSubscriptionDto) {
		logger.info("Started - Calling create Subscription details {}", createSubscriptionDto);
		try {
			SuccessDto successDto = this.subscriptionService
					.createSubscription(
							this.subscriptionMapper.createSubscriptionDtoToSubscription(createSubscriptionDto));
			logger.info("Completed - Calling create Subscription details {} with id {} ", createSubscriptionDto,
					successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.CREATED);
		} catch (BusinessException be) {
			logger.error("Unable to create Subscription {} with BusinessException {} ", createSubscriptionDto,
					be.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIException(
					String.valueOf(HttpStatus.CONFLICT), ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION));
		} catch (Exception e) {
			logger.error("Unable to create Subscription {} with Exception {}", createSubscriptionDto, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/update")
	public ResponseEntity<?> update(@NotNull @Valid @RequestBody SubscriptionDto subscriptionDto) {
		logger.info("Started - Calling update Subscription details {}", subscriptionDto);
		try {
			SuccessDto successDto = this.subscriptionService
					.updateSubscription(this.subscriptionMapper.subscriptionDtoToSubscription(subscriptionDto));
			logger.info("Completed - Calling update Subscription by Subscription details {} with id {}",
					subscriptionDto, successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of Subscription id {} with BusinessException {} ",
					subscriptionDto.getId(),
					be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIException(
						HttpStatus.BAD_REQUEST.toString(), ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION));

			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED));

			} else {

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}

		} catch (Exception e) {
			logger.error("Unable to get details of Subscription id {} with Exception {} ", subscriptionDto.getId(),
					e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/delete/{id}")
	public ResponseEntity<?> delete(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling delete Subscription details by id {}", id);
		try {
			SuccessDto successDto = this.subscriptionService.deleteSubscription(id);
			logger.info("Completed - Calling delete Subscription by id {}", successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to delete Subscription id {} with BusinessException {} ", id, be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new APIException(HttpStatus.BAD_REQUEST.toString(),
								ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION));
			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new APIException(HttpStatus.NOT_FOUND.toString(),
								ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to delete Subscription id {} with Exception {}", id, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

}

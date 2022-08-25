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

import com.coderlook.chatrachatri.subscriptions.dto.CreateSubscriptionHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.SubscriptionHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.entity.SubscriptionHistory;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.SubscriptionHistoryMapper;
import com.coderlook.chatrachatri.subscriptions.service.SubscriptionHistoryService;

@RestController
@RequestMapping("/api/subscription-history")
public class SubscriptionHistoryController {
	@Autowired
	private SubscriptionHistoryService subscriptionHistoryService;

	@Autowired
	private SubscriptionHistoryMapper subscriptionHistoryMapper;

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionHistoryController.class);

	@GetMapping("/{id}")
	public ResponseEntity<?> getSubscriptionHistory(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling getSubscriptionHistory by subscriptionHistory id {}", id);
		try {
			SubscriptionHistoryDto subscriptionHistoryDto = this.subscriptionHistoryMapper
					.subscriptionHistoryToSubscriptionHistoryDto(
							this.subscriptionHistoryService.getSubscriptionHistoryById(id));
			logger.info(
					"Completed - SubscriptionHistory details found {} while calling getSubscriptionHistory by id {}",
					subscriptionHistoryDto, id);
			return new ResponseEntity<SubscriptionHistoryDto>(subscriptionHistoryDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of subscriptionHistory id {} with BusinessException {} ", id,
					be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to get details of subscriptionHistory id {} with Exception {} ", id, e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> getAllSubscriptionHistorys(
			@RequestParam(value = "plan", required = false) Optional<String> planName,
			@RequestParam(value = "subscriptionId", required = false) Optional<Long> subscriptionId,
			@RequestParam(value = "status", required = false) Optional<Status> status,
			@RequestParam(value = "pageNumber", required = false) Optional<Integer> pageNumber,
			@RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize,
			@RequestParam(value = "sort", required = false) Optional<String> sort) {
		logger.info("Started - Calling getAllSubscriptionHistory");
		try {
			Page<SubscriptionHistory> result = this.subscriptionHistoryService.search(planName, subscriptionId, status,
					pageNumber, pageSize, sort);
			Page<SubscriptionHistoryDto> subscriptionHistoryPageDto = result
					.map(subscriptionHistory -> this.subscriptionHistoryMapper
							.subscriptionHistoryToSubscriptionHistoryDto(subscriptionHistory));
			logger.info("Completed - Record found {} while calling getAllSubscriptionHistorys",
					result.getTotalElements());
			return new ResponseEntity<Page<SubscriptionHistoryDto>>(subscriptionHistoryPageDto, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Unable to fetch list of SubscriptionHistory with Exception {} ", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/create")
	public ResponseEntity<?> create(
			@NotNull @Valid @RequestBody CreateSubscriptionHistoryDto createSubscriptionHistoryDto) {
		logger.info("Started - Calling create subscriptionHistory details {}", createSubscriptionHistoryDto);
		try {
			SuccessDto successDto = this.subscriptionHistoryService
					.createSubscriptionHistory(
							this.subscriptionHistoryMapper
									.createSubscriptionHistoryDtoToSubscriptionHistory(createSubscriptionHistoryDto));
			logger.info("Completed - Calling create SubscriptionHistory details {} with id {} ",
					createSubscriptionHistoryDto,
					successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.CREATED);
		} catch (BusinessException be) {
			logger.error("Unable to create SubscriptionHistory {} with BusinessException {} ",
					createSubscriptionHistoryDto,
					be.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIException(
					String.valueOf(HttpStatus.CONFLICT),
					ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION_HISTORY));
		} catch (Exception e) {
			logger.error("Unable to create SubscriptionHistory {} with Exception {}", createSubscriptionHistoryDto,
					e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/update")
	public ResponseEntity<?> update(@NotNull @Valid @RequestBody SubscriptionHistoryDto subscriptionHistoryDto) {
		logger.info("Started - Calling update subscriptionHistory details {}", subscriptionHistoryDto);
		try {
			SuccessDto successDto = this.subscriptionHistoryService
					.updateSubscriptionHistory(this.subscriptionHistoryMapper
							.subscriptionHistoryDtoToSubscriptionHistory(subscriptionHistoryDto));
			logger.info("Completed - Calling update subscriptionHistory by subscriptionHistory details {} with id {}",
					subscriptionHistoryDto, successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of subscriptionHistory id {} with BusinessException {} ",
					subscriptionHistoryDto.getId(),
					be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIException(
						HttpStatus.BAD_REQUEST.toString(),
						ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION_HISTORY));

			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED));

			} else {

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}

		} catch (Exception e) {
			logger.error("Unable to get details of SubscriptionHistory id {} with Exception {} ",
					subscriptionHistoryDto.getId(),
					e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/delete/{id}")
	public ResponseEntity<?> delete(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling delete SubscriptionHistory details by id {}", id);
		try {
			SuccessDto successDto = this.subscriptionHistoryService.deleteSubscriptionHistory(id);
			logger.info("Completed - Calling delete SubscriptionHistory by id {}", successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to delete SubscriptionHistory id {} with BusinessException {} ", id, be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new APIException(HttpStatus.BAD_REQUEST.toString(),
								ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION_HISTORY));
			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new APIException(HttpStatus.NOT_FOUND.toString(),
								ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to delete SubscriptionHistory id {} with Exception {}", id, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

}

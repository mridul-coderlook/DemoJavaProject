package com.coderlook.chatrachatri.subscriptions.controller;

import java.time.LocalDate;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coderlook.chatrachatri.subscriptions.dto.CreatePaymentHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.PaymentHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.PaymentHistory;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.PaymentHistoryMapper;
import com.coderlook.chatrachatri.subscriptions.service.PaymentHistoryService;

@RestController
@RequestMapping("/api/payment-history")
public class PaymentHistoryController {
	@Autowired
	private PaymentHistoryService paymentHistoryService;

	@Autowired
	private PaymentHistoryMapper paymentHistoryMapper;

	private static final Logger logger = LoggerFactory.getLogger(PaymentHistoryController.class);

	@GetMapping("/{id}")
	public ResponseEntity<?> getPaymentHistory(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling getPaymentHistory by paymentHistory id {}", id);
		try {
			PaymentHistoryDto paymentHistoryDto = this.paymentHistoryMapper
					.paymentHistoryToPaymentHistoryDto(
							this.paymentHistoryService.getPaymentHistoryById(id));
			logger.info(
					"Completed - paymentHistory details found {} while calling getPaymentHistory by id {}",
					paymentHistoryDto, id);
			return new ResponseEntity<PaymentHistoryDto>(paymentHistoryDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of paymentHistory id {} with BusinessException {} ", id,
					be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to get details of paymentHistory id {} with Exception {} ", id, e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> getAllPaymentHistorys(
			@RequestParam(value = "plan", required = false) Optional<String> planName,
			@RequestParam(value = "transaction", required = false) Optional<String> transaction,
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate,
			@RequestParam(value = "paymentType", required = false) Optional<String> paymentType,
			@RequestParam(value = "paymentStatus", required = false) Optional<String> paymentStatus,
			@RequestParam(value = "pageNumber", required = false) Optional<Integer> pageNumber,
			@RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize,
			@RequestParam(value = "sort", required = false) Optional<String> sort) {
		logger.info("Started - Calling getAllPaymentHistory");
		try {
			Page<PaymentHistory> result = this.paymentHistoryService.search(planName, transaction, startDate, endDate,
					paymentType, paymentStatus, pageNumber, pageSize, sort);
			Page<PaymentHistoryDto> paymentHistoryPageDto = result
					.map(paymentHistory -> this.paymentHistoryMapper
							.paymentHistoryToPaymentHistoryDto(paymentHistory));
			logger.info("Completed - Record found {} while calling getAllPaymentHistorys",
					result.getTotalElements());
			return new ResponseEntity<Page<PaymentHistoryDto>>(paymentHistoryPageDto, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Unable to fetch list of paymentHistory with Exception {} ", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/create")
	public ResponseEntity<?> create(
			@NotNull @Valid @RequestBody CreatePaymentHistoryDto createPaymentHistoryDto) {
		logger.info("Started - Calling create PaymentHistory details {}", createPaymentHistoryDto);
		try {
			SuccessDto successDto = this.paymentHistoryService
					.createPaymentHistory(this.paymentHistoryMapper
							.createPaymentHistoryDtoToPaymentHistory(createPaymentHistoryDto));
			logger.info("Completed - Calling create PaymentHistory details {} with id {} ",
					createPaymentHistoryDto, successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.CREATED);
		} catch (BusinessException be) {
			logger.error("Unable to create paymentHistory {} with BusinessException {} ",
					createPaymentHistoryDto, be.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIException(
					String.valueOf(HttpStatus.CONFLICT),
					ExceptionMessageConstants.FAILED_TO_CREATE_PAYMENT_HISTORY));
		} catch (Exception e) {
			logger.error("Unable to create paymentHistory {} with Exception {}", createPaymentHistoryDto,
					e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/update")
	public ResponseEntity<?> update(@NotNull @Valid @RequestBody PaymentHistoryDto paymentHistoryDto) {
		logger.info("Started - Calling update PaymentHistory details {}", paymentHistoryDto);
		try {
			SuccessDto successDto = this.paymentHistoryService
					.updatePaymentHistory(this.paymentHistoryMapper
							.paymentHistoryDtoToPaymentHistory(paymentHistoryDto));
			logger.info("Completed - Calling update paymentHistory by paymentHistory details {} with id {}",
					paymentHistoryDto, successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of paymentHistory id {} with BusinessException {} ",
					paymentHistoryDto.getId(), be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIException(
						HttpStatus.BAD_REQUEST.toString(),
						ExceptionMessageConstants.FAILED_TO_UPDATE_PAYMENT_HISTORY));

			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED));

			} else {

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}

		} catch (Exception e) {
			logger.error("Unable to get details of paymentHistory id {} with Exception {} ",
					paymentHistoryDto.getId(),
					e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/delete/{id}")
	public ResponseEntity<?> delete(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling delete PaymentHistory details by id {}", id);
		try {
			SuccessDto successDto = this.paymentHistoryService.deletePaymentHistory(id);
			logger.info("Completed - Calling delete paymentHistory by id {}", successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to delete paymentHistory id {} with BusinessException {} ", id, be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new APIException(HttpStatus.BAD_REQUEST.toString(),
								ExceptionMessageConstants.FAILED_TO_DELETE_PAYMENT_HISTORY));
			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new APIException(HttpStatus.NOT_FOUND.toString(),
								ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to delete paymentHistory id {} with Exception {}", id, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

}

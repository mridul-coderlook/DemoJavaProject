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

import com.coderlook.chatrachatri.subscriptions.dto.CreateOrderRequestDto;
import com.coderlook.chatrachatri.subscriptions.dto.OrderRequestDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.OrderRequest;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.OrderRequestMapper;
import com.coderlook.chatrachatri.subscriptions.service.OrderRequestService;

@RestController
@RequestMapping("/api/order-request")
public class OrderRequestController {
	@Autowired
	private OrderRequestService orderRequestService;

	@Autowired
	private OrderRequestMapper orderRequestMapper;

	private static final Logger logger = LoggerFactory.getLogger(OrderRequestController.class);

	@GetMapping("/{id}")
	public ResponseEntity<?> getOrderRequest(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling getOrderRequest by orderRequest id {}", id);
		try {
			OrderRequestDto orderRequestDto = this.orderRequestMapper
					.orderRequestToOrderRequestDto(this.orderRequestService.getOrderRequestById(id));
			logger.info("Completed - OrderRequest details found {} while calling getOrderRequest by id {}",
					orderRequestDto, id);
			return new ResponseEntity<OrderRequestDto>(orderRequestDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of OrderRequest id {} with BusinessException {} ", id, be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to get details of orderRequest id {} with Exception {} ", id, e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> getSearchOrderRequests(
			@RequestParam(value = "code", required = false) Optional<String> planName,
			@RequestParam(value = "name", required = false) Optional<Integer> duration,
			@RequestParam(value = "status", required = false) Optional<Status> status,
			@RequestParam(value = "pageNumber", required = false) Optional<Integer> pageNumber,
			@RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize,
			@RequestParam(value = "sort", required = false) Optional<String> sort) {
		logger.info("Started - Calling getSearchOrderRequests");
		try {
			Page<OrderRequest> result = this.orderRequestService.search(planName, duration, status, pageNumber,
					pageSize, sort);
			Page<OrderRequestDto> orderRequestPageDto = result
					.map(orderRequest -> this.orderRequestMapper.orderRequestToOrderRequestDto(orderRequest));
			logger.info("Completed - Record found {} while calling getSearchOrderRequests", result.getTotalElements());
			return new ResponseEntity<Page<OrderRequestDto>>(orderRequestPageDto, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Unable to fetch list of orderRequest with Exception {} ", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/create")
	public ResponseEntity<?> create(
			@NotNull @Valid @RequestBody CreateOrderRequestDto createOrderRequestDto) {
		logger.info("Started - Calling create orderRequest details {}", createOrderRequestDto);
		try {
			SuccessDto successDto = this.orderRequestService
					.createOrderRequest(
							this.orderRequestMapper.createOrderRequestDtoToOrderRequest(createOrderRequestDto));
			logger.info("Completed - Calling create orderRequest details {} with id {} ", createOrderRequestDto,
					successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.CREATED);
		} catch (BusinessException be) {
			logger.error("Unable to create orderRequest {} with BusinessException {} ", createOrderRequestDto,
					be.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIException(
					String.valueOf(HttpStatus.CONFLICT), ExceptionMessageConstants.FAILED_TO_CREATE_ORDER_REQUEST));
		} catch (Exception e) {
			logger.error("Unable to create orderRequest {} with Exception {}", createOrderRequestDto, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/update")
	public ResponseEntity<?> update(@NotNull @Valid @RequestBody OrderRequestDto orderRequestDto) {
		logger.info("Started - Calling update OrderRequest details {}", orderRequestDto);
		try {
			SuccessDto successDto = this.orderRequestService
					.updateOrderRequest(this.orderRequestMapper.orderRequestDtoToOrderRequest(orderRequestDto));
			logger.info("Completed - Calling update OrderRequest by OrderRequest details {} with id {}",
					orderRequestDto, successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of OrderRequest id {} with BusinessException {} ",
					orderRequestDto.getId(),
					be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIException(
						HttpStatus.BAD_REQUEST.toString(), ExceptionMessageConstants.FAILED_TO_UPDATE_ORDER_REQUEST));

			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED));

			} else {

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}

		} catch (Exception e) {
			logger.error("Unable to get details of OrderRequest id {} with Exception {} ", orderRequestDto.getId(),
					e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/delete/{id}")
	public ResponseEntity<?> delete(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling delete OrderRequest details by id {}", id);
		try {
			SuccessDto successDto = this.orderRequestService.deleteOrderRequest(id);
			logger.info("Completed - Calling delete OrderRequest by id {}", successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to delete OrderRequest id {} with BusinessException {} ", id, be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new APIException(HttpStatus.BAD_REQUEST.toString(),
								ExceptionMessageConstants.FAILED_TO_DELETE_ORDER_REQUEST));
			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new APIException(HttpStatus.NOT_FOUND.toString(),
								ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to delete OrderRequest id {} with Exception {}", id, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

}

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

import com.coderlook.chatrachatri.subscriptions.dto.CreateTrialHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.dto.TrialHistoryDto;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.entity.TrialHistory;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.TrialHistoryMapper;
import com.coderlook.chatrachatri.subscriptions.service.TrialHistoryService;

@RestController
@RequestMapping("/api/trial-history")
public class TrialHistoryController {
	@Autowired
	private TrialHistoryService trialHistoryService;

	@Autowired
	private TrialHistoryMapper trialHistoryMapper;

	private static final Logger logger = LoggerFactory.getLogger(TrialHistoryController.class);

	@GetMapping("/{id}")
	public ResponseEntity<?> getTrialHistory(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling getTrialHistory by trialHistory id {}", id);
		try {
			TrialHistoryDto trialHistoryDto = this.trialHistoryMapper
					.trialHistoryToTrialHistoryDto(
							this.trialHistoryService.getTrialHistoryById(id));
			logger.info(
					"Completed - trialHistory details found {} while calling getTrialHistory by id {}",
					trialHistoryDto, id);
			return new ResponseEntity<TrialHistoryDto>(trialHistoryDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of trialHistory id {} with BusinessException {} ", id,
					be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to get details of trialHistory id {} with Exception {} ", id, e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> getSearchTrialHistorys(
			@RequestParam(value = "plan", required = false) Optional<String> planName,
			@RequestParam(value = "status", required = false) Optional<Status> status,
			@RequestParam(value = "pageNumber", required = false) Optional<Integer> pageNumber,
			@RequestParam(value = "pageSize", required = false) Optional<Integer> pageSize,
			@RequestParam(value = "sort", required = false) Optional<String> sort) {
		logger.info("Started - Calling getSearchTrialHistorys");
		try {
			Page<TrialHistory> result = this.trialHistoryService.search(planName, status, pageNumber, pageSize, sort);
			Page<TrialHistoryDto> trialHistoryPageDto = result
					.map(trialHistory -> this.trialHistoryMapper
							.trialHistoryToTrialHistoryDto(trialHistory));
			logger.info("Completed - Record found {} while calling getSearchTrialHistorys",
					result.getTotalElements());
			return new ResponseEntity<Page<TrialHistoryDto>>(trialHistoryPageDto, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Unable to fetch list of trialHistory with Exception {} ", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/create")
	public ResponseEntity<?> create(
			@NotNull @Valid @RequestBody CreateTrialHistoryDto createTrialHistoryDto) {
		logger.info("Started - Calling create trialHistory details {}", createTrialHistoryDto);
		try {
			SuccessDto successDto = this.trialHistoryService
					.createTrialHistory(this.trialHistoryMapper
							.createTrialHistoryDtoToTrialHistory(createTrialHistoryDto));
			logger.info("Completed - Calling create trialHistory details {} with id {} ",
					createTrialHistoryDto, successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.CREATED);
		} catch (BusinessException be) {
			logger.error("Unable to create trialHistory {} with BusinessException {} ",
					createTrialHistoryDto,
					be.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIException(
					String.valueOf(HttpStatus.CONFLICT),
					ExceptionMessageConstants.FAILED_TO_CREATE_TRIAL_HISTORY));
		} catch (Exception e) {
			logger.error("Unable to create trialHistory {} with Exception {}", createTrialHistoryDto,
					e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/update")
	public ResponseEntity<?> update(@NotNull @Valid @RequestBody TrialHistoryDto trialHistoryDto) {
		logger.info("Started - Calling update trialHistory details {}", trialHistoryDto);
		try {
			SuccessDto successDto = this.trialHistoryService
					.updateTrialHistory(this.trialHistoryMapper
							.trialHistoryDtoToTrialHistory(trialHistoryDto));
			logger.info("Completed - Calling update trialHistory by trialHistory details {} with id {}",
					trialHistoryDto, successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to get details of trialHistory id {} with BusinessException {} ",
					trialHistoryDto.getId(), be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIException(
						HttpStatus.BAD_REQUEST.toString(),
						ExceptionMessageConstants.FAILED_TO_UPDATE_TRIAL_HISTORY));

			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIException(
						HttpStatus.NOT_FOUND.toString(),
						ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED));

			} else {

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}

		} catch (Exception e) {
			logger.error("Unable to get details of trialHistory id {} with Exception {} ",
					trialHistoryDto.getId(),
					e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/delete/{id}")
	public ResponseEntity<?> delete(@NotNull @PathVariable(value = "id", required = true) Long id) {
		logger.info("Started - Calling delete trialHistory details by id {}", id);
		try {
			SuccessDto successDto = this.trialHistoryService.deleteTrialHistory(id);
			logger.info("Completed - Calling delete trialHistory by id {}", successDto.getId());
			return new ResponseEntity<SuccessDto>(successDto, HttpStatus.OK);
		} catch (BusinessException be) {
			logger.error("Unable to delete trialHistory id {} with BusinessException {} ", id, be.getMessage());
			if (be.getStatus().equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new APIException(HttpStatus.BAD_REQUEST.toString(),
								ExceptionMessageConstants.FAILED_TO_DELETE_TRIAL_HISTORY));
			} else if (be.getStatus().equals(String.valueOf(HttpStatus.NOT_FOUND))) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new APIException(HttpStatus.NOT_FOUND.toString(),
								ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
						HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
			}
		} catch (Exception e) {
			logger.error("Unable to delete trialHistory id {} with Exception {}", id, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIException(
					HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));
		}
	}

}

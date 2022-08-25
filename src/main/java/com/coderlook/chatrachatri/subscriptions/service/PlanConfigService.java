package com.coderlook.chatrachatri.subscriptions.service;

import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.PlanConfig;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.repository.PlanConfigRepository;
import com.coderlook.chatrachatri.subscriptions.util.ChatrachatriConstants;

@Service
public class PlanConfigService {

	private static final Logger logger = LoggerFactory.getLogger(PlanConfigService.class);

	// private final static int DEFAULT_PAGE_SIZE = 10;

	@Autowired
	private PlanConfigRepository planConfigRepository;

	/**
	 * Get PlanConfig Details
	 * 
	 * @param id
	 * @return PlanConfig
	 */
	@Transactional
	public PlanConfig getPlanConfigById(Long id) throws BusinessException {
		Optional<PlanConfig> planConfigOptional = this.planConfigRepository.findById(id);
		if (planConfigOptional.isPresent()) {
			logger.debug("Completed - Record found while calling getPlanConfigById by PlanConfig id {}", id);
			return planConfigOptional.get();
		} else {
			logger.error("Unable to get PlanConfig while calling getPlanConfigById by PlanConfig id {}", id);
			throw new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
					ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED,
					ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED);
		}
	}

	/**
	 * Create PlanConfig Details
	 * 
	 * @param PlanConfig
	 * @return PlanConfig Details
	 * @throws BusinessException
	 */
	@Transactional
	public SuccessDto createPlanConfig(PlanConfig planConfig) throws BusinessException {

		planConfig.setStatus(Status.Draft);
		planConfig.setCreatedBy(1L);
		planConfig.setUpdatedBy(1L);
		planConfig.setCreatedAt(new Date());
		planConfig.setUpdatedAt(new Date());
		planConfig = this.planConfigRepository.save(planConfig);
		if (planConfig.getId() != null && planConfig.getId() > 0) {
			logger.debug("Completed - create PlanConfig while calling createPlanConfig by planConfigDetails {}",
					planConfig);
			return new SuccessDto(planConfig.getId(), ChatrachatriConstants.CREATED_SUCCESS_MESSAGE);
		} else {
			logger.error("Unable to create PlanConfig while calling createPlanConfig by planConfigDetails {}",
					planConfig);
			throw new BusinessException(String.valueOf(HttpStatus.CONFLICT),
					ExceptionMessageConstants.FAILED_TO_CREATE_PLAN_CONFIG,
					ExceptionMessageConstants.FAILED_TO_CREATE_PLAN_CONFIG);

		}
	}

	/**
	 * Update PlanConfig Details
	 * 
	 * @param id
	 * @param PlanConfig
	 * @return PlanConfig
	 * @throws BusinessException
	 */
	@Transactional
	public SuccessDto updatePlanConfig(PlanConfig planConfig) throws BusinessException {

		// get PlanConfig details by id
		PlanConfig newPlanConfig = this.getPlanConfigById(planConfig.getId());
		// set PlanConfig's updated information
		newPlanConfig.setPlan(planConfig.getPlan());
		newPlanConfig.setFeature(planConfig.getFeature());
		newPlanConfig.setCount(planConfig.getCount());
		newPlanConfig.setDisplayOrder(planConfig.getDisplayOrder());
		newPlanConfig.setUpdatedAt(new Date());
		newPlanConfig.setStatus(planConfig.getStatus());
		newPlanConfig = this.planConfigRepository.save(newPlanConfig);

		if (newPlanConfig != null) {
			logger.debug("Completed - Updated PlanConfig while calling updatePlanConfig by planConfig id {}",
					planConfig.getId());
			return new SuccessDto(planConfig.getId(), ChatrachatriConstants.UPDATED_SUCCESS_MESSAGE);
		} else {
			logger.error("Unable to update PlanConfig details by planConfig id {} ", planConfig.getId());
			throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
					ExceptionMessageConstants.FAILED_TO_UPDATE_PLAN_CONFIG,
					ExceptionMessageConstants.FAILED_TO_UPDATE_PLAN_CONFIG);
		}
	}

	/**
	 * Delete PlanConfig Details
	 * 
	 * @param id
	 * @return Boolean status
	 */
	@Transactional
	public SuccessDto deletePlanConfig(Long id) throws BusinessException {

		// get PlanConfig details by id
		PlanConfig newPlanConfig = this.getPlanConfigById(id);
		// set PlanConfig's updated information
		newPlanConfig.setUpdatedAt(new Date());
		newPlanConfig.setStatus(Status.Deleted);
		newPlanConfig = this.planConfigRepository.save(newPlanConfig);
		if (newPlanConfig != null) {
			logger.debug("Completed - Removed PlanConfig while calling deletePlanConfig by planConfig id {}", id);
			return new SuccessDto(id, ChatrachatriConstants.REMOVED_SUCCESS_MESSAGE);
		} else {
			logger.error("Unable to remove PlanConfig while calling deletePlanConfig by planConfig id {}", id);
			throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
					ExceptionMessageConstants.FAILED_TO_DELETE_PLAN_CONFIG,
					ExceptionMessageConstants.FAILED_TO_DELETE_PLAN_CONFIG);
		}
	}

}

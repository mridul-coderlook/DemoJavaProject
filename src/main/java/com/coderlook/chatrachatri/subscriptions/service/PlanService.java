package com.coderlook.chatrachatri.subscriptions.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.Plan;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.repository.PlanRepository;
import com.coderlook.chatrachatri.subscriptions.util.ChatrachatriConstants;

@Service
public class PlanService {

	private static final Logger logger = LoggerFactory.getLogger(PlanService.class);
	
	private final static int DEFAULT_PAGE_SIZE = 10;
	
	@Autowired
	private PlanRepository planRepository;

	/**
	 * Get Plan Details
	 * 
	 * @param id
	 * @return Plan
	 */
	@Transactional
	public Plan getPlanById(Long id) throws BusinessException {
		Optional<Plan> planOptional = this.planRepository.findById(id);
		if (planOptional.isPresent()) {
			logger.debug("Completed - Record found while calling getPlanById by Plan id {}", id);
			return planOptional.get();
		} else {
			logger.error("Unable to get Plan while calling getPlanById by Plan id {}", id);
			throw new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
					ExceptionMessageConstants.NOT_FOUND_PLAN_ID_IS_REQUIRED,
					ExceptionMessageConstants.NOT_FOUND_PLAN_ID_IS_REQUIRED);
		}
	}

	/**
	 * Create Plan Details
	 * 
	 * @param Plan
	 * @return Plan Details
	 * @throws BusinessException
	 */
	@Transactional
	public SuccessDto createPlan(Plan plan) throws BusinessException {

		plan.setStatus(Status.Draft);
		plan.setCreatedBy(1L);
		plan.setUpdatedBy(1L);
		plan.setCreatedAt(new Date());
		plan.setUpdatedAt(new Date());
		plan = this.planRepository.save(plan);
		if (plan.getId() != null && plan.getId() > 0) {
			logger.debug("Completed - create Plan while calling createPlan by PlanDetails {}",
					plan);
			return new SuccessDto(plan.getId(), ChatrachatriConstants.CREATED_SUCCESS_MESSAGE);
		} else {
			logger.error("Unable to create Plan while calling createPlan by PlanDetails {}",
					plan);
			throw new BusinessException(String.valueOf(HttpStatus.CONFLICT),
					ExceptionMessageConstants.FAILED_TO_CREATE_PLAN,
					ExceptionMessageConstants.FAILED_TO_CREATE_PLAN);

		}
	}

	/**
	 * Update Plan Details
	 * 
	 * @param id
	 * @param Plan
	 * @return Plan
	 * @throws BusinessException
	 */
	@Transactional
	public SuccessDto updatePlan(Plan plan) throws BusinessException {

		// get Plan details by id
		Plan newPlan = this.getPlanById(plan.getId());
		// set Plan's updated information
		newPlan.setName(plan.getName());
		newPlan.setCode(plan.getCode());
		newPlan.setDescription(plan.getDescription());
		newPlan.setPrice(plan.getPrice());
		newPlan.setDuration(plan.getDuration());
		newPlan.setFrequency(plan.getFrequency());
		newPlan.setWeight(plan.getWeight());
		newPlan.setUpdatedAt(new Date());
		newPlan.setStatus(plan.getStatus());
		newPlan = this.planRepository.save(newPlan);

		if (newPlan != null) {
			logger.debug("Completed - Updated Plan while calling updatePlan by Plan id {}", plan.getId());
			return new SuccessDto(plan.getId(), ChatrachatriConstants.UPDATED_SUCCESS_MESSAGE);
		} else {
			logger.error("Unable to update Plan details by Plan id {} ", plan.getId());
			throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
					ExceptionMessageConstants.FAILED_TO_UPDATE_PLAN,
					ExceptionMessageConstants.FAILED_TO_UPDATE_PLAN);
		}
	}

	/**
	 * Delete Plan Details
	 * 
	 * @param id
	 * @return Boolean status
	 */
	@Transactional
	public SuccessDto deletePlan(Long id) throws BusinessException {

		// get Plan details by id
		Plan newPlan = this.getPlanById(id);
		// set Plan's updated information
		newPlan.setUpdatedAt(new Date());
		newPlan.setStatus(Status.Deleted);
		newPlan = this.planRepository.save(newPlan);
		if (newPlan != null) {
			logger.debug("Completed - Removed Plan while calling deletePlan by Plan id {}", id);
			return new SuccessDto(id, ChatrachatriConstants.REMOVED_SUCCESS_MESSAGE);
		} else {
			logger.error("Unable to remove Plan while calling deletePlan by Plan id {}", id);
			throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
					ExceptionMessageConstants.FAILED_TO_DELETE_PLAN,
					ExceptionMessageConstants.FAILED_TO_DELETE_PLAN);
		}
	}

	/**
	 * Search Plan
	 * 
	 * @param name
	 * @param code
	 * @param status
	 * @param pageNumber
	 * @param pageSize
	 * @param sort
	 * @return Page<PlanDto>
	 */
	@Transactional
	public Page<Plan> search(
			Optional<String> name,
			Optional<String> code,
			Optional<Status> status,
			Optional<Integer> pageNumber,
			Optional<Integer> pageSize,
			Optional<String> sort) {

		logger.debug("Started - Calling search");

		int page = pageNumber.isPresent() ? pageNumber.get() : 0;
		int size = pageSize.isPresent() ? pageSize.get() : DEFAULT_PAGE_SIZE;

		Sort sortDir = Sort.by(Sort.Direction.DESC, "updatedAt");
		if (sort.isPresent() && sort.equals("DESC")) {
			sortDir = Sort.by(Sort.Direction.ASC, "updatedAt");
		}
		Pageable paging = PageRequest.of(page, size, sortDir);

		Page<Plan> planPage = this.planRepository.findAll(new Specification<Plan>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Plan> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = new ArrayList<>();

				if (name.isPresent() && !name.isEmpty()) {
					predicates.add(criteriaBuilder.or(
							criteriaBuilder.like(root.get("name"), "%" + name.get().trim() + "%")));
				}
				if (code.isPresent() && !code.isEmpty()) {
					predicates.add(
							criteriaBuilder.like(root.get("code"), "%" + code.get().trim() + "%"));
				}

				if (status.isPresent() && !status.isEmpty())
					predicates.add(criteriaBuilder.equal(root.get("status"), status.get()));

				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		}, paging);

		return planPage;
	}

}

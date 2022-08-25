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
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.entity.Subscription;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.repository.SubscriptionRepository;
import com.coderlook.chatrachatri.subscriptions.util.ChatrachatriConstants;

@Service
public class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    private final static int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    /**
     * Get Subscription Details
     * 
     * @param id
     * @return Subscription
     */
    @Transactional
    public Subscription getSubscriptionById(Long id) throws BusinessException {
        Optional<Subscription> subscriptionOptional = this.subscriptionRepository.findById(id);
        if (subscriptionOptional.isPresent()) {
            logger.debug("Completed - Record found while calling getSubscriptionById by subscription id {}", id);
            return subscriptionOptional.get();
        } else {
            logger.error("Unable to get Subscription while calling getSubscriptionById by subscription id {}", id);
            throw new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
                    ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED,
                    ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED);
        }
    }

    /**
     * Create Subscription Details
     * 
     * @param Subscription
     * @return Subscription Details
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto createSubscription(Subscription subscription) throws BusinessException {

        subscription.setStatus(Status.Draft);
        subscription.setCreatedBy(1L);
        subscription.setUpdatedBy(1L);
        subscription.setCreatedAt(new Date());
        subscription.setUpdatedAt(new Date());
        subscription = this.subscriptionRepository.save(subscription);
        if (subscription.getId() != null && subscription.getId() > 0) {
            logger.debug("Completed - create Subscription while calling createSubscription by SubscriptionDetails {}",
                    subscription);
            return new SuccessDto(subscription.getId(), ChatrachatriConstants.CREATED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to create Subscription while calling createSubscription by SubscriptionDetails {}",
                    subscription);
            throw new BusinessException(String.valueOf(HttpStatus.CONFLICT),
                    ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION,
                    ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION);

        }
    }

    /**
     * Update Subscription Details
     * 
     * @param id
     * @param Subscription
     * @return Subscription
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto updateSubscription(Subscription subscription) throws BusinessException {

        // get Subscription details by id
        Subscription newSubscription = this.getSubscriptionById(subscription.getId());
        // set Subscription's updated information
        newSubscription.setEndDate(subscription.getEndDate());
        newSubscription.setInstitutionId(subscription.getInstitutionId());
        newSubscription.setNoOfStudent(subscription.getNoOfStudent());
        newSubscription.setOrderRequest(subscription.getOrderRequest());
        newSubscription.setPlan(subscription.getPlan());
        newSubscription.setStartDate(subscription.getStartDate());
        newSubscription.setStatus(subscription.getStatus());
        newSubscription.setTotalPrice(subscription.getTotalPrice());
        newSubscription.setTrial(subscription.getTrial());
        newSubscription.setUpdatedAt(new Date());
        newSubscription.setStatus(subscription.getStatus());
        newSubscription = this.subscriptionRepository.save(newSubscription);

        if (newSubscription != null) {
            logger.debug("Completed - Updated Subscription while calling updateSubscription by Subscription id {}",
                    subscription.getId());
            return new SuccessDto(subscription.getId(), ChatrachatriConstants.UPDATED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to update Subscription details by Subscription id {} ", subscription.getId());
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION,
                    ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION);
        }
    }

    /**
     * Delete Subscription Details
     * 
     * @param id
     * @return Boolean status
     */
    @Transactional
    public SuccessDto deleteSubscription(Long id) throws BusinessException {

        // get Subscription details by id
        Subscription newSubscription = this.getSubscriptionById(id);
        // set Subscription's updated information
        newSubscription.setUpdatedAt(new Date());
        newSubscription.setStatus(Status.Deleted);
        newSubscription = this.subscriptionRepository.save(newSubscription);
        if (newSubscription != null) {
            logger.debug("Completed - Removed Subscription while calling deleteSubscription by Subscription id {}", id);
            return new SuccessDto(id, ChatrachatriConstants.REMOVED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to remove Subscription while calling deleteSubscription by Subscription id {}", id);
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION,
                    ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION);
        }
    }

    /**
     * Search Subscription
     * 
     * @param name
     * @param code
     * @param status
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @return Page<SubscriptionDto>
     */
    @Transactional
    public Page<Subscription> search(
            Optional<String> planName,
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

        Page<Subscription> subscriptionPage = this.subscriptionRepository.findAll(new Specification<Subscription>() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Subscription> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                if (planName.isPresent() && !planName.isEmpty()) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get("plan").get("name"), "%" + planName.get().trim() + "%")));
                }

                if (status.isPresent() && !status.isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status.get()));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, paging);

        return subscriptionPage;
    }

}

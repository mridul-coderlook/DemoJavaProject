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
import com.coderlook.chatrachatri.subscriptions.entity.SubscriptionHistory;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.repository.SubscriptionHistoryRepository;
import com.coderlook.chatrachatri.subscriptions.util.ChatrachatriConstants;

@Service
public class SubscriptionHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionHistoryService.class);

    private final static int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private SubscriptionHistoryRepository subscriptionHistoryRepository;

    /**
     * Get SubscriptionHistory Details
     * 
     * @param id
     * @return SubscriptionHistory
     */
    @Transactional
    public SubscriptionHistory getSubscriptionHistoryById(Long id) throws BusinessException {
        Optional<SubscriptionHistory> subscriptionHistoryOptional = this.subscriptionHistoryRepository.findById(id);
        if (subscriptionHistoryOptional.isPresent()) {
            logger.debug(
                    "Completed - Record found while calling getSubscriptionHistoryById by subscriptionHistory id {}",
                    id);
            return subscriptionHistoryOptional.get();
        } else {
            logger.error(
                    "Unable to get subscriptionHistory while calling getSubscriptionHistoryById by subscriptionHistory id {}",
                    id);
            throw new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
                    ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED,
                    ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED);
        }
    }

    /**
     * Create SubscriptionHistory Details
     * 
     * @param SubscriptionHistory
     * @return SubscriptionHistory Details
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto createSubscriptionHistory(SubscriptionHistory subscriptionHistory) throws BusinessException {

        subscriptionHistory.setCreatedBy(1L);
        subscriptionHistory.setUpdatedBy(1L);
        subscriptionHistory.setCreatedAt(new Date());
        subscriptionHistory.setUpdatedAt(new Date());
        subscriptionHistory = this.subscriptionHistoryRepository.save(subscriptionHistory);
        if (subscriptionHistory.getId() != null && subscriptionHistory.getId() > 0) {
            logger.debug(
                    "Completed - create SubscriptionHistory while calling createSubscriptionHistory by SubscriptionHistoryDetails {}",
                    subscriptionHistory);
            return new SuccessDto(subscriptionHistory.getId(), ChatrachatriConstants.CREATED_SUCCESS_MESSAGE);
        } else {
            logger.error(
                    "Unable to create SubscriptionHistory while calling createSubscriptionHistory by SubscriptionHistoryDetails {}",
                    subscriptionHistory);
            throw new BusinessException(String.valueOf(HttpStatus.CONFLICT),
                    ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION_HISTORY,
                    ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION_HISTORY);

        }
    }

    /**
     * Update SubscriptionHistory Details
     * 
     * @param id
     * @param SubscriptionHistory
     * @return SubscriptionHistory
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto updateSubscriptionHistory(SubscriptionHistory subscriptionHistory) throws BusinessException {

        // get SubscriptionHistory details by id
        SubscriptionHistory newSubscriptionHistory = this.getSubscriptionHistoryById(subscriptionHistory.getId());
        // set SubscriptionHistory's updated information
        newSubscriptionHistory.setEndDate(subscriptionHistory.getEndDate());
        newSubscriptionHistory.setInstitutionId(subscriptionHistory.getInstitutionId());
        newSubscriptionHistory.setNoOfStudent(subscriptionHistory.getNoOfStudent());
        newSubscriptionHistory.setOrderRequest(subscriptionHistory.getOrderRequest());
        newSubscriptionHistory.setSubscription(subscriptionHistory.getSubscription());
        newSubscriptionHistory.setPlan(subscriptionHistory.getPlan());
        newSubscriptionHistory.setStartDate(subscriptionHistory.getStartDate());
        newSubscriptionHistory.setStatus(subscriptionHistory.getStatus());
        newSubscriptionHistory.setTotalPrice(subscriptionHistory.getTotalPrice());
        newSubscriptionHistory.setTrial(subscriptionHistory.getTrial());
        newSubscriptionHistory.setUpdatedAt(new Date());
        newSubscriptionHistory = this.subscriptionHistoryRepository.save(newSubscriptionHistory);

        if (newSubscriptionHistory != null) {
            logger.debug(
                    "Completed - Updated subscriptionHistory while calling updateSubscriptionHistory by subscriptionHistory id {}",
                    newSubscriptionHistory.getId());
            return new SuccessDto(newSubscriptionHistory.getId(), ChatrachatriConstants.UPDATED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to update subscriptionHistory details by subscriptionHistory id {} ",
                    subscriptionHistory.getId());
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION_HISTORY,
                    ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION_HISTORY);
        }
    }

    /**
     * Delete SubscriptionHistory Details
     * 
     * @param id
     * @return Boolean status
     */
    @Transactional
    public SuccessDto deleteSubscriptionHistory(Long id) throws BusinessException {

        // get SubscriptionHistory details by id
        SubscriptionHistory newSubscriptionHistory = this.getSubscriptionHistoryById(id);
        // set SubscriptionHistory's updated information
        newSubscriptionHistory.setUpdatedAt(new Date());
        newSubscriptionHistory.setStatus(Status.Deleted);
        newSubscriptionHistory = this.subscriptionHistoryRepository.save(newSubscriptionHistory);
        if (newSubscriptionHistory != null) {
            logger.debug(
                    "Completed - Removed subscriptionHistory while calling deleteSubscriptionHistory by subscriptionHistory id {}",
                    id);
            return new SuccessDto(id, ChatrachatriConstants.REMOVED_SUCCESS_MESSAGE);
        } else {
            logger.error(
                    "Unable to remove subscriptionHistory while calling deleteSubscriptionHistory by subscriptionHistory id {}",
                    id);
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION_HISTORY,
                    ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION_HISTORY);
        }
    }

    /**
     * Search SubscriptionHistory
     * 
     * @param name
     * @param code
     * @param status
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @return Page<SubscriptionHistoryDto>
     */
    @Transactional
    public Page<SubscriptionHistory> search(
            Optional<String> planName,
            Optional<Long> subscriptionId,
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

        Page<SubscriptionHistory> subscriptionHistoryPage = this.subscriptionHistoryRepository
                .findAll(new Specification<SubscriptionHistory>() {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Predicate toPredicate(Root<SubscriptionHistory> root, CriteriaQuery<?> query,
                            CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();

                        if (planName.isPresent() && !planName.isEmpty()) {
                            predicates.add(criteriaBuilder.or(
                                    criteriaBuilder.like(root.get("plan").get("name"),
                                            "%" + planName.get().trim() + "%")));
                        }

                        if (subscriptionId.isPresent() && !subscriptionId.isEmpty()) {
                            predicates.add(criteriaBuilder.equal(root.get("subscription").get("id"),
                                    subscriptionId.get()));
                        }

                        if (status.isPresent() && !status.isEmpty()) {
                            predicates.add(criteriaBuilder.equal(root.get("status"), status.get()));
                        }

                        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                }, paging);

        return subscriptionHistoryPage;
    }

}

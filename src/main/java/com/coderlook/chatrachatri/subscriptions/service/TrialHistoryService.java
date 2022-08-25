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
import com.coderlook.chatrachatri.subscriptions.entity.TrialHistory;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.repository.TrialHistoryRepository;
import com.coderlook.chatrachatri.subscriptions.util.ChatrachatriConstants;

@Service
public class TrialHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(TrialHistoryService.class);

    private final static int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private TrialHistoryRepository trialHistoryRepository;

    /**
     * Get TrialHistory Details
     * 
     * @param id
     * @return TrialHistory
     */
    @Transactional
    public TrialHistory getTrialHistoryById(Long id) throws BusinessException {
        Optional<TrialHistory> trialHistoryOptional = this.trialHistoryRepository.findById(id);
        if (trialHistoryOptional.isPresent()) {
            logger.debug(
                    "Completed - Record found while calling getTrialHistoryById by trialHistory id {}",
                    id);
            return trialHistoryOptional.get();
        } else {
            logger.error(
                    "Unable to get trialHistory while calling getTrialHistoryById by trialHistory id {}",
                    id);
            throw new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
                    ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED,
                    ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED);
        }
    }

    /**
     * Create TrialHistory Details
     * 
     * @param TrialHistory
     * @return TrialHistory Details
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto createTrialHistory(TrialHistory trialHistory) throws BusinessException {

        trialHistory.setStatus(Status.Draft);
        trialHistory.setCreatedBy(1L);
        trialHistory.setUpdatedBy(1L);
        trialHistory.setCreatedAt(new Date());
        trialHistory.setUpdatedAt(new Date());
        trialHistory = this.trialHistoryRepository.save(trialHistory);
        if (trialHistory.getId() != null && trialHistory.getId() > 0) {
            logger.debug(
                    "Completed - create TrialHistory while calling createTrialHistory by trialHistoryDetails {}",
                    trialHistory);
            return new SuccessDto(trialHistory.getId(), ChatrachatriConstants.CREATED_SUCCESS_MESSAGE);
        } else {
            logger.error(
                    "Unable to create TrialHistory while calling createTrialHistory by trialHistoryDetails {}",
                    trialHistory);
            throw new BusinessException(String.valueOf(HttpStatus.CONFLICT),
                    ExceptionMessageConstants.FAILED_TO_CREATE_TRIAL_HISTORY,
                    ExceptionMessageConstants.FAILED_TO_CREATE_TRIAL_HISTORY);

        }
    }

    /**
     * Update TrialHistory Details
     * 
     * @param id
     * @param TrialHistory
     * @return TrialHistory
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto updateTrialHistory(TrialHistory trialHistory) throws BusinessException {

        // get TrialHistory details by id
        TrialHistory newTrialHistory = this.getTrialHistoryById(trialHistory.getId());
        // set TrialHistory's updated information
        newTrialHistory.setInstitutionId(trialHistory.getInstitutionId());
        newTrialHistory.setPlan(trialHistory.getPlan());
        newTrialHistory.setSubscriptionHistory(trialHistory.getSubscriptionHistory());
        newTrialHistory.setStatus(trialHistory.getStatus());
        newTrialHistory.setUpdatedAt(new Date());
        newTrialHistory = this.trialHistoryRepository.save(newTrialHistory);

        if (newTrialHistory != null) {
            logger.debug(
                    "Completed - Updated TrialHistory while calling updateTrialHistory by trialHistory id {}",
                    newTrialHistory.getId());
            return new SuccessDto(newTrialHistory.getId(), ChatrachatriConstants.UPDATED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to update trialHistory details by trialHistory id {} ",
                    trialHistory.getId());
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_UPDATE_TRIAL_HISTORY,
                    ExceptionMessageConstants.FAILED_TO_UPDATE_TRIAL_HISTORY);
        }
    }

    /**
     * Delete TrialHistory Details
     * 
     * @param id
     * @return Boolean status
     */
    @Transactional
    public SuccessDto deleteTrialHistory(Long id) throws BusinessException {

        // get TrialHistory details by id
        TrialHistory newTrialHistory = this.getTrialHistoryById(id);
        // set TrialHistory's updated information
        newTrialHistory.setUpdatedAt(new Date());
        newTrialHistory.setStatus(Status.Deleted);
        newTrialHistory = this.trialHistoryRepository.save(newTrialHistory);
        if (newTrialHistory != null) {
            logger.debug(
                    "Completed - Removed trialHistory while calling deleteTrialHistory by trialHistory id {}",
                    id);
            return new SuccessDto(id, ChatrachatriConstants.REMOVED_SUCCESS_MESSAGE);
        } else {
            logger.error(
                    "Unable to remove trialHistory while calling deleteTrialHistory by trialHistory id {}",
                    id);
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_DELETE_TRIAL_HISTORY,
                    ExceptionMessageConstants.FAILED_TO_DELETE_TRIAL_HISTORY);
        }
    }

    /**
     * Search TrialHistory
     * 
     * @param name
     * @param code
     * @param status
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @return Page<TrialHistoryDto>
     */
    @Transactional
    public Page<TrialHistory> search(
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

        Page<TrialHistory> trialHistoryPage = this.trialHistoryRepository.findAll(new Specification<TrialHistory>() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<TrialHistory> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                if (planName.isPresent() && !planName.isEmpty()) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get("plan").get("name"),
                                    "%" + planName.get().trim() + "%")));
                }

                if (status.isPresent() && !status.isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status.get()));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, paging);

        return trialHistoryPage;
    }

}

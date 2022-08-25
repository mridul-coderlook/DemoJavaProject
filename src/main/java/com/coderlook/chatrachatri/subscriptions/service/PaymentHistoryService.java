package com.coderlook.chatrachatri.subscriptions.service;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.coderlook.chatrachatri.subscriptions.entity.PaymentHistory;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.repository.PaymentHistoryRepository;
import com.coderlook.chatrachatri.subscriptions.util.ChatrachatriConstants;

@Service
public class PaymentHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentHistoryService.class);

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    private final static int DEFAULT_PAGE_SIZE = 10;

    /**
     * Get PaymentHistory Details
     * 
     * @param id
     * @return PaymentHistory
     */
    @Transactional
    public PaymentHistory getPaymentHistoryById(Long id) throws BusinessException {
        Optional<PaymentHistory> paymentHistoryOptional = this.paymentHistoryRepository.findById(id);
        if (paymentHistoryOptional.isPresent()) {
            logger.debug(
                    "Completed - Record found while calling getPaymentHistoryById by paymentHistory id {}",
                    id);
            return paymentHistoryOptional.get();
        } else {
            logger.error(
                    "Unable to get paymentHistory while calling getPaymentHistoryById by paymentHistory id {}",
                    id);
            throw new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
                    ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED,
                    ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED);
        }
    }

    /**
     * Create PaymentHistory Details
     * 
     * @param PaymentHistory
     * @return PaymentHistory Details
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto createPaymentHistory(PaymentHistory paymentHistory) throws BusinessException {

        paymentHistory = this.paymentHistoryRepository.save(paymentHistory);
        if (paymentHistory.getId() != null && paymentHistory.getId() > 0) {
            logger.debug(
                    "Completed - create paymentHistory while calling createPaymentHistory by paymentHistoryDetails {}",
                    paymentHistory);
            return new SuccessDto(paymentHistory.getId(), ChatrachatriConstants.CREATED_SUCCESS_MESSAGE);
        } else {
            logger.error(
                    "Unable to create PaymentHistory while calling createPaymentHistory by paymentHistoryDetails {}",
                    paymentHistory);
            throw new BusinessException(String.valueOf(HttpStatus.CONFLICT),
                    ExceptionMessageConstants.FAILED_TO_CREATE_PAYMENT_HISTORY,
                    ExceptionMessageConstants.FAILED_TO_CREATE_PAYMENT_HISTORY);

        }
    }

    /**
     * Update PaymentHistory Details
     * 
     * @param id
     * @param PaymentHistory
     * @return PaymentHistory
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto updatePaymentHistory(PaymentHistory paymentHistory) throws BusinessException {

        // get PaymentHistory details by id
        PaymentHistory newPaymentHistory = this.getPaymentHistoryById(paymentHistory.getId());
        // set PaymentHistory's updated information
        newPaymentHistory.setAmount(paymentHistory.getAmount());
        newPaymentHistory.setOrderRequest(paymentHistory.getOrderRequest());
        newPaymentHistory.setPaymentDate(paymentHistory.getPaymentDate());
        newPaymentHistory.setPaymentType(paymentHistory.getPaymentType());
        newPaymentHistory.setPaymentStatus(paymentHistory.getPaymentStatus());
        newPaymentHistory.setPlan(paymentHistory.getPlan());
        newPaymentHistory.setTransaction(paymentHistory.getTransaction());
        newPaymentHistory = this.paymentHistoryRepository.save(newPaymentHistory);

        if (newPaymentHistory != null) {
            logger.debug(
                    "Completed - Updated paymentHistory while calling updatePaymentHistory by paymentHistory id {}",
                    newPaymentHistory.getId());
            return new SuccessDto(newPaymentHistory.getId(), ChatrachatriConstants.UPDATED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to update paymentHistory details by paymentHistory id {} ",
                    paymentHistory.getId());
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_UPDATE_PAYMENT_HISTORY,
                    ExceptionMessageConstants.FAILED_TO_UPDATE_PAYMENT_HISTORY);
        }
    }

    /**
     * Delete PaymentHistory Details
     * 
     * @param id
     * @return Boolean status
     */
    @Transactional
    public SuccessDto deletePaymentHistory(Long id) throws BusinessException {

        // get PaymentHistory details by id
        PaymentHistory newPaymentHistory = this.getPaymentHistoryById(id);
        this.paymentHistoryRepository.delete(newPaymentHistory);
        if (newPaymentHistory != null) {
            logger.debug(
                    "Completed - Removed PaymentHistory while calling deletePaymentHistory by PaymentHistory id {}",
                    id);
            return new SuccessDto(id, ChatrachatriConstants.REMOVED_SUCCESS_MESSAGE);
        } else {
            logger.error(
                    "Unable to remove PaymentHistory while calling deletePaymentHistory by PaymentHistory id {}",
                    id);
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_DELETE_PAYMENT_HISTORY,
                    ExceptionMessageConstants.FAILED_TO_DELETE_PAYMENT_HISTORY);
        }
    }

    /**
     * Search PaymentHistory
     * 
     * @param planName
     * @param transaction
     * @param startDate
     * @param endDate
     * @param paymentType
     * @param paymentStatus
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @return Page<PaymentHistory>
     */
    @Transactional
    public Page<PaymentHistory> search(
            Optional<String> planName,
            Optional<String> transaction,
            Optional<LocalDate> startDate,
            Optional<LocalDate> endDate,
            Optional<String> paymentType,
            Optional<String> paymentStatus,
            Optional<Integer> pageNumber,
            Optional<Integer> pageSize,
            Optional<String> sort) {

        logger.debug("Started - Calling search");

        int page = pageNumber.isPresent() ? pageNumber.get() : 0;
        int size = pageSize.isPresent() ? pageSize.get() : DEFAULT_PAGE_SIZE;

        Sort sortDir = Sort.by(Sort.Direction.DESC, "id");
        if (sort.isPresent() && sort.equals("ASC")) {
            sortDir = Sort.by(Sort.Direction.ASC, "id");
        }
        Pageable paging = PageRequest.of(page, size, sortDir);

        Page<PaymentHistory> paymentHistoryPage = this.paymentHistoryRepository
                .findAll(new Specification<PaymentHistory>() {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Predicate toPredicate(Root<PaymentHistory> root, CriteriaQuery<?> query,
                            CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();

                        if (planName.isPresent() && !planName.isEmpty()) {
                            predicates.add(criteriaBuilder.or(
                                    criteriaBuilder.like(root.get("plan").get("name"),
                                            "%" + planName.get().trim() + "%")));
                        }

                        if (transaction.isPresent() && !transaction.isEmpty()) {
                            predicates.add(criteriaBuilder.or(
                                    criteriaBuilder.like(root.get("transaction"),
                                            "%" + transaction.get().trim() + "%")));
                        }

                        if (startDate.isPresent() && endDate.isPresent()) {
                            predicates.add(criteriaBuilder.or(
                                    criteriaBuilder.between(root.get("paymentDate"), startDate.get(), endDate.get())));
                        }

                        if (paymentType.isPresent() && !paymentType.isEmpty()) {
                            predicates.add(criteriaBuilder.or(
                                    criteriaBuilder.like(root.get("paymentType"),
                                            "%" + paymentType.get().trim() + "%")));
                        }

                        if (paymentStatus.isPresent() && !paymentStatus.isEmpty()) {
                            predicates.add(criteriaBuilder.or(
                                    criteriaBuilder.like(root.get("paymentStatus"),
                                            "%" + paymentStatus.get().trim() + "%")));
                        }

                        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                }, paging);

        return paymentHistoryPage;
    }

}

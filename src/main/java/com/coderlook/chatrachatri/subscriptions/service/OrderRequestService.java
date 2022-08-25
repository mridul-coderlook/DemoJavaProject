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
import com.coderlook.chatrachatri.subscriptions.entity.OrderRequest;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.repository.OrderRequestRepository;
import com.coderlook.chatrachatri.subscriptions.util.ChatrachatriConstants;

@Service
public class OrderRequestService {

    private static final Logger logger = LoggerFactory.getLogger(OrderRequestService.class);

    private final static int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private OrderRequestRepository orderRequestRepository;

    /**
     * Get OrderRequest Details
     * 
     * @param id
     * @return OrderRequest
     */
    @Transactional
    public OrderRequest getOrderRequestById(Long id) throws BusinessException {
        Optional<OrderRequest> orderRequestOptional = this.orderRequestRepository.findById(id);
        if (orderRequestOptional.isPresent()) {
            logger.debug("Completed - Record found while calling getOrderRequestById by orderRequest id {}", id);
            return orderRequestOptional.get();
        } else {
            logger.error("Unable to get OrderRequest while calling getOrderRequestById by orderRequest id {}", id);
            throw new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
                    ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED,
                    ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED);
        }
    }

    /**
     * Create OrderRequest Details
     * 
     * @param OrderRequest
     * @return OrderRequest Details
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto createOrderRequest(OrderRequest orderRequest) throws BusinessException {

        orderRequest.setStatus(Status.Draft);
        orderRequest.setCreatedBy(1L);
        orderRequest.setUpdatedBy(1L);
        orderRequest.setCreatedAt(new Date());
        orderRequest.setUpdatedAt(new Date());
        orderRequest = this.orderRequestRepository.save(orderRequest);
        if (orderRequest.getId() != null && orderRequest.getId() > 0) {
            logger.debug("Completed - create OrderRequest while calling createOrderRequest by orderRequestDetails {}",
                    orderRequest);
            return new SuccessDto(orderRequest.getId(), ChatrachatriConstants.CREATED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to create OrderRequest while calling createOrderRequest by orderRequestDetails {}",
                    orderRequest);
            throw new BusinessException(String.valueOf(HttpStatus.CONFLICT),
                    ExceptionMessageConstants.FAILED_TO_CREATE_ORDER_REQUEST,
                    ExceptionMessageConstants.FAILED_TO_CREATE_ORDER_REQUEST);

        }
    }

    /**
     * Update OrderRequest Details
     * 
     * @param id
     * @param OrderRequest
     * @return OrderRequest
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto updateOrderRequest(OrderRequest orderRequest) throws BusinessException {

        // get OrderRequest details by id
        OrderRequest newOrderRequest = this.getOrderRequestById(orderRequest.getId());
        // set OrderRequest's updated information
        newOrderRequest.setCouponCode(orderRequest.getCouponCode());
        newOrderRequest.setDiscount(orderRequest.getDiscount());
        newOrderRequest.setDiscountPercentage(orderRequest.getDiscountPercentage());
        newOrderRequest.setDuration(orderRequest.getDuration());
        newOrderRequest.setNoOfUser(orderRequest.getNoOfUser());
        newOrderRequest.setOrderDate(orderRequest.getOrderDate());
        newOrderRequest.setPlan(orderRequest.getPlan());
        newOrderRequest.setPrice(orderRequest.getPrice());
        newOrderRequest.setStatus(orderRequest.getStatus());
        newOrderRequest.setUnitPrice(orderRequest.getUnitPrice());
        newOrderRequest.setUpdatedAt(new Date());
        newOrderRequest.setUpgrade(orderRequest.getUpgrade());
        newOrderRequest = this.orderRequestRepository.save(newOrderRequest);

        if (newOrderRequest != null) {
            logger.debug("Completed - Updated OrderRequest while calling updateOrderRequest by orderRequest id {}",
                    orderRequest.getId());
            return new SuccessDto(orderRequest.getId(), ChatrachatriConstants.UPDATED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to update OrderRequest details by orderRequest id {} ", orderRequest.getId());
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_UPDATE_ORDER_REQUEST,
                    ExceptionMessageConstants.FAILED_TO_UPDATE_ORDER_REQUEST);
        }
    }

    /**
     * Delete OrderRequest Details
     * 
     * @param id
     * @return Boolean status
     */
    @Transactional
    public SuccessDto deleteOrderRequest(Long id) throws BusinessException {

        // get OrderRequest details by id
        OrderRequest newOrderRequest = this.getOrderRequestById(id);
        // set OrderRequest's updated information
        newOrderRequest.setUpdatedAt(new Date());
        newOrderRequest.setStatus(Status.Deleted);
        newOrderRequest = this.orderRequestRepository.save(newOrderRequest);
        if (newOrderRequest != null) {
            logger.debug("Completed - Removed OrderRequest while calling deleteOrderRequest by orderRequest id {}", id);
            return new SuccessDto(id, ChatrachatriConstants.REMOVED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to remove OrderRequest while calling deleteOrderRequest by orderRequest id {}", id);
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_DELETE_ORDER_REQUEST,
                    ExceptionMessageConstants.FAILED_TO_DELETE_ORDER_REQUEST);
        }
    }

    /**
     * Search OrderRequest
     * 
     * @param name
     * @param code
     * @param status
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @return Page<OrderRequestDto>
     */
    @Transactional
    public Page<OrderRequest> search(
            Optional<String> planName,
            Optional<Integer> duration,
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

        Page<OrderRequest> orderRequestPage = this.orderRequestRepository.findAll(new Specification<OrderRequest>() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<OrderRequest> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                if (planName.isPresent() && !planName.isEmpty()) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get("plan").get("name"), "%" + planName.get().trim() + "%")));
                }

                if (duration.isPresent() && !duration.isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("duration"), duration.get()));
                }

                if (status.isPresent() && !status.isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status.get()));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, paging);

        return orderRequestPage;
    }

}

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
import com.coderlook.chatrachatri.subscriptions.entity.Feature;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.repository.FeatureRepository;
import com.coderlook.chatrachatri.subscriptions.util.ChatrachatriConstants;

@Service
public class FeatureService {

    private static final Logger logger = LoggerFactory.getLogger(FeatureService.class);

    private final static int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private FeatureRepository featureRepository;

    /**
     * Get Feature Details
     * 
     * @param id
     * @return Feature
     */
    @Transactional
    public Feature getFeatureById(Long id) throws BusinessException {
        Optional<Feature> featureOptional = this.featureRepository.findById(id);
        if (featureOptional.isPresent()) {
            logger.debug("Completed - Record found while calling getFeatureById by feature id {}", id);
            return featureOptional.get();
        } else {
            logger.error("Unable to get Feature while calling getFeatureById by feature id {}", id);
            throw new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
                    ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED,
                    ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED);
        }
    }

    /**
     * Create Feature Details
     * 
     * @param Feature
     * @return Feature Details
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto createFeature(Feature feature) throws BusinessException {

        feature.setStatus(Status.Draft);
        feature.setCreatedBy(1L);
        feature.setUpdatedBy(1L);
        feature.setCreatedAt(new Date());
        feature.setUpdatedAt(new Date());
        feature = this.featureRepository.save(feature);
        if (feature.getId() != null && feature.getId() > 0) {
            logger.debug("Completed - create Feature while calling createFeature by featureDetails {}",
                    feature);
            return new SuccessDto(feature.getId(), ChatrachatriConstants.CREATED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to create Feature while calling createFeature by featureDetails {}",
                    feature);
            throw new BusinessException(String.valueOf(HttpStatus.CONFLICT),
                    ExceptionMessageConstants.FAILED_TO_CREATE_FEATURE,
                    ExceptionMessageConstants.FAILED_TO_CREATE_FEATURE);

        }
    }

    /**
     * Update Feature Details
     * 
     * @param id
     * @param Feature
     * @return Feature
     * @throws BusinessException
     */
    @Transactional
    public SuccessDto updateFeature(Feature feature) throws BusinessException {

        // get Feature details by id
        Feature newFeature = this.getFeatureById(feature.getId());
        // set Feature's updated information
        newFeature.setDisplayText(feature.getDisplayText());
        newFeature.setName(feature.getName());
        newFeature.setMarkEnabled(feature.getMarkEnabled());
        newFeature.setShowInDisplay(feature.getShowInDisplay());
        newFeature.setRequired(feature.getRequired());
        newFeature.setUpdatedAt(new Date());
        newFeature.setStatus(feature.getStatus());
        newFeature = this.featureRepository.save(newFeature);

        if (newFeature != null) {
            logger.debug("Completed - Updated Feature while calling updateFeature by feature id {}",
                    feature.getId());
            return new SuccessDto(feature.getId(), ChatrachatriConstants.UPDATED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to update feature details by Feature id {} ", feature.getId());
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_UPDATE_FEATURE,
                    ExceptionMessageConstants.FAILED_TO_UPDATE_FEATURE);
        }
    }

    /**
     * Delete Feature Details
     * 
     * @param id
     * @return Boolean status
     */
    @Transactional
    public SuccessDto deleteFeature(Long id) throws BusinessException {

        // get Feature details by id
        Feature newFeature = this.getFeatureById(id);
        // set Feature's updated information
        newFeature.setUpdatedAt(new Date());
        newFeature.setStatus(Status.Deleted);
        newFeature = this.featureRepository.save(newFeature);
        if (newFeature != null) {
            logger.debug("Completed - Removed Feature while calling deleteFeature by feature id {}", id);
            return new SuccessDto(id, ChatrachatriConstants.REMOVED_SUCCESS_MESSAGE);
        } else {
            logger.error("Unable to remove feature while calling deleteFeature by feature id {}", id);
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                    ExceptionMessageConstants.FAILED_TO_DELETE_FEATURE,
                    ExceptionMessageConstants.FAILED_TO_DELETE_FEATURE);
        }
    }

    /**
     * Search Feature
     * 
     * @param name
     * @param displayText
     * @param markEnabled
     * @param showInDisplay
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @return Page<Feature>
     */
    @Transactional
    public Page<Feature> search(
            Optional<String> name,
            Optional<String> displayText,
            Optional<Boolean> markEnabled,
            Optional<Boolean> showInDisplay,
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

        Page<Feature> featurePage = this.featureRepository.findAll(new Specification<Feature>() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Feature> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                if (name.isPresent() && !name.isEmpty()) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get("name"), "%" + name.get().trim() + "%")));
                }

                if (displayText.isPresent() && !displayText.isEmpty()) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get("displayText"), "%" + displayText.get().trim() + "%")));
                }

                if (markEnabled.isPresent() && !markEnabled.isEmpty()) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("markEnabled"), markEnabled.get())));
                }

                if (markEnabled.isPresent() && !markEnabled.isEmpty()) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("markEnabled"), markEnabled.get())));
                }

                if (status.isPresent() && !status.isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status.get()));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, paging);

        return featurePage;
    }

}

package com.coderlook.chatrachatri.subscriptions.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coderlook.chatrachatri.subscriptions.entity.TrialHistory;

@Repository
public interface TrialHistoryRepository extends JpaRepository<TrialHistory, Long> {

    Page<TrialHistory> findAll(Specification<TrialHistory> specification, Pageable paging);

}

package com.coderlook.chatrachatri.subscriptions.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coderlook.chatrachatri.subscriptions.entity.SubscriptionHistory;

@Repository
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {

    Page<SubscriptionHistory> findAll(Specification<SubscriptionHistory> specification, Pageable paging);

}

package com.coderlook.chatrachatri.subscriptions.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coderlook.chatrachatri.subscriptions.entity.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

	Page<Plan> findAll(Specification<Plan> specification, Pageable paging);

}

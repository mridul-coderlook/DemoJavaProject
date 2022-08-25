package com.coderlook.chatrachatri.subscriptions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coderlook.chatrachatri.subscriptions.entity.PlanConfig;

@Repository
public interface PlanConfigRepository extends JpaRepository<PlanConfig, Long> {

}

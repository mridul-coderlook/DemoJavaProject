package com.coderlook.chatrachatri.subscriptions.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionHistoryDto {

    private Long id;
    private PlanDto plan;
    private OrderRequestDto orderRequest;
    private SubscriptionDto subscription;
    private Long institutionId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalPrice;
    private Integer noOfStudent;
    private Boolean trial;
    private String action;

}

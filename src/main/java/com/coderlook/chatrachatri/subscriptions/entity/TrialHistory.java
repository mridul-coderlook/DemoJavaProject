package com.coderlook.chatrachatri.subscriptions.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties({ "hibernateLazyInitializer" })
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trial_history")
public class TrialHistory extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 223357908266879780L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "institution_id")
	private Long institutionId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan")
	private Plan plan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_history")
	private SubscriptionHistory subscriptionHistory;

}

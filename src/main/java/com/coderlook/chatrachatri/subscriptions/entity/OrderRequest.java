package com.coderlook.chatrachatri.subscriptions.entity;

import java.io.Serializable;
import java.time.LocalDate;

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
@Table(name = "order_request")
public class OrderRequest extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1315244147788615780L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan")
	private Plan plan;

	@Column(name = "order_date")
	private LocalDate orderDate;

	@Column(name = "no_of_user")
	private Integer noOfUser;

	@Column(name = "duration")
	private Integer duration;

	@Column(name = "price")
	private Double price;

	@Column(name = "upgrade")
	private Boolean upgrade;

	@Column(name = "discount")
	private Double discount;

	@Column(name = "coupon_code")
	private String couponCode;

	@Column(name = "discount_percentage")
	private Double discountPercentage;

	@Column(name = "unit_price")
	private Double unitPrice;

}

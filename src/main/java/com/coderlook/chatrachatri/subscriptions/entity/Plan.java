package com.coderlook.chatrachatri.subscriptions.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name = "plan")
public class Plan extends BaseEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -259461579203420574L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "code")
	private String code;

	@Lob
	@Column(name = "description")
	private String description;

	@Column(name = "price")
	private Double price;

	@Column(name = "is_free")
	private Boolean free;

	@Column(name = "weight")
	private Integer weight;

	@Column(name = "frequency")
	private String frequency;

	@Column(name = "duration")
	private Integer duration;

}

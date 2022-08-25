package com.coderlook.chatrachatri.subscriptions.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "feature")
public class Feature extends BaseEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1685602643660439505L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "display_text")
	private String displayText;

	@Column(name = "mark_enabled")
	private Boolean markEnabled;

	@Column(name = "show_in_display")
	private Boolean showInDisplay;

	@Column(name = "is_required")
	private Boolean required;

}

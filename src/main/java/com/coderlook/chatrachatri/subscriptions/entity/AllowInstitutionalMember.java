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
@Table(name = "allowed_no_of_institutional_members")
public class AllowInstitutionalMember extends BaseEntity implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2273868656177635526L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

    @Column(name = "no_of_members")
	private Integer noOfMembers;

    @Column(name = "institution_id")
	private Long institutionId;

    
}

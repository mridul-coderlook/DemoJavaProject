package com.coderlook.chatrachatri.subscriptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllowInstitutionalMemberDto {

	private Long id;
	private Integer noOfMembers;
	private Long institutionId;

    
}

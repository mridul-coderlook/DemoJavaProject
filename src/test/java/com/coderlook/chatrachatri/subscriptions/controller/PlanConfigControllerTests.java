package com.coderlook.chatrachatri.subscriptions.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.coderlook.chatrachatri.subscriptions.dto.CreatePlanConfigDto;
import com.coderlook.chatrachatri.subscriptions.dto.PlanConfigDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.PlanConfig;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.PlanConfigMapper;
import com.coderlook.chatrachatri.subscriptions.service.PlanConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class PlanConfigControllerTests {

	@InjectMocks
	public PlanConfigController planConfigController;

	@Mock
	private PlanConfigService planConfigService;

	@Mock
	private PlanConfigMapper planConfigMapper;

	@Autowired
	private MockMvc mockMvc;

	private static ObjectMapper mapper = new ObjectMapper();

	private String uriToFetchById = "/api/plan-config/{id}";
	private String uriToCreate = "/api/plan-config/create";
	private String uriToUpdate = "/api/plan-config/update";
	private String uriToDelete = "/api/plan-config/delete/{id}";

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(planConfigController).build();

	}

	// Test Case for PlanConfig Details

	@Test
	void testGetPlanConfigDetails_basic() throws Exception {

		PlanConfigDto planConfigDto = new PlanConfigDto();
		planConfigDto.setId(1L);
		planConfigDto.setCount(10);

		when(this.planConfigService.getPlanConfigById(Mockito.anyLong())).thenReturn(
				new PlanConfig());

		when(this.planConfigMapper.planConfigToPlanConfigDto(
				this.planConfigService.getPlanConfigById(Mockito.anyLong()))).thenReturn(
						planConfigDto);

		this.mockMvc.perform(get(uriToFetchById, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.count").value(10))
				.andReturn();

	}

	@Test
	void testGetPlanConfig_internal_server_error() throws Exception {

		when(planConfigService.getPlanConfigById(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
				ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		this.mockMvc.perform(get(uriToFetchById, 1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testGetPlanConfig_notFound() throws Exception {

		when(this.planConfigService.getPlanConfigById(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED));

		this.mockMvc.perform(get(uriToFetchById, 1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED));

	}

	// Test Case for PlanConfig Create

	@Test
	void testPlanConfigCreate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Created Success");

		when(planConfigService.createPlanConfig(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreatePlanConfigDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Created Success"));

	}

	@Test
	void testPlanConfigCreate_internal_server_error() throws Exception {

		when(planConfigService.createPlanConfig(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreatePlanConfigDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testPlanConfigCreate_conflict() throws Exception {

		when(planConfigService.createPlanConfig(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.CONFLICT),
						ExceptionMessageConstants.FAILED_TO_CREATE_PLAN_CONFIG,
						ExceptionMessageConstants.FAILED_TO_CREATE_PLAN_CONFIG));

		MockHttpServletRequestBuilder mockRequest = post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreatePlanConfigDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isConflict())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_CREATE_PLAN_CONFIG));

	}

	// Test Case for Update
	@Test
	void testPlanConfigUpdate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Updated Success");

		when(planConfigService.updatePlanConfig(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PlanConfigDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Updated Success"));

	}

	@Test
	void testPlanConfigUpdate_internal_server_error() throws Exception {

		when(planConfigService.updatePlanConfig(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PlanConfigDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testPlanConfigUpdate_badRequest() throws Exception {

		when(planConfigService.updatePlanConfig(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_UPDATE_PLAN_CONFIG,
						ExceptionMessageConstants.FAILED_TO_UPDATE_PLAN_CONFIG));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PlanConfigDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_UPDATE_PLAN_CONFIG));

	}

	@Test
	void testPlanConfigUpdate_notFound() throws Exception {

		when(planConfigService.updatePlanConfig(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PlanConfigDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED));

	}

	// Test Case For PlanConfig Delete

	@Test
	void testPlanConfigDelete_Basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Removed Success");

		when(planConfigService.deletePlanConfig(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(dto));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Removed Success"));
	}

	@Test
	void testPlanConfigDelete_internal_server_error() throws Exception {

		when(planConfigService.deletePlanConfig(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
				ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PlanConfigDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testPlanConfigDelete_badRequest() throws Exception {

		when(planConfigService.deletePlanConfig(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_DELETE_PLAN_CONFIG,
						ExceptionMessageConstants.FAILED_TO_DELETE_PLAN_CONFIG));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PlanConfigDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_DELETE_PLAN_CONFIG));

	}

	@Test
	void testPlanConfigDelete_notFound() throws Exception {

		when(planConfigService.deletePlanConfig(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PlanConfigDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_PLAN_CONFIG_ID_IS_REQUIRED));

	}

}

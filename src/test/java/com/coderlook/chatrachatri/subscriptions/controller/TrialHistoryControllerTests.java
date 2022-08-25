package com.coderlook.chatrachatri.subscriptions.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.coderlook.chatrachatri.subscriptions.dto.CreateTrialHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.TrialHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.TrialHistory;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.TrialHistoryMapper;
import com.coderlook.chatrachatri.subscriptions.service.TrialHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class TrialHistoryControllerTests {

	@InjectMocks
	public TrialHistoryController trialHistoryController;

	@Mock
	private TrialHistoryService trialHistoryService;

	@Mock
	private TrialHistoryMapper trialHistoryMapper;

	@Autowired
	private MockMvc mockMvc;

	private static ObjectMapper mapper = new ObjectMapper();

	private String uriToFetchById = "/api/trial-history/{id}";
	private String uriToCreate = "/api/trial-history/create";
	private String uriToUpdate = "/api/trial-history/update";
	private String uriToDelete = "/api/trial-history/delete/{id}";
	private String uriToFetchList = "/api/trial-history/search";

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(trialHistoryController).build();

	}

	// Test Case for TrialHistory Details

	@Test
	void testGetTrialHistoryDetails_basic() throws Exception {

		TrialHistoryDto trialHistoryDto = new TrialHistoryDto();
		trialHistoryDto.setId(1L);
		trialHistoryDto.setInstitutionId(2L);

		when(this.trialHistoryService.getTrialHistoryById(Mockito.anyLong())).thenReturn(
				new TrialHistory());

		when(this.trialHistoryMapper.trialHistoryToTrialHistoryDto(
				this.trialHistoryService.getTrialHistoryById(Mockito.anyLong()))).thenReturn(
						trialHistoryDto);

		this.mockMvc.perform(get(uriToFetchById, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.institutionId").value(2L))
				.andReturn();

	}

	@Test
	void testGetTrialHistory_internal_server_error() throws Exception {

		when(trialHistoryService.getTrialHistoryById(Mockito.any())).thenThrow(new APIException(
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
	void testGetTrialHistory_notFound() throws Exception {

		when(this.trialHistoryService.getTrialHistoryById(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED));

		this.mockMvc.perform(get(uriToFetchById, 1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED));

	}

	// Test Case for TrialHistory Create

	@Test
	void testTrialHistoryCreate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Created Success");

		when(trialHistoryService.createTrialHistory(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateTrialHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Created Success"));

	}

	@Test
	void testTrialHistoryCreate_internal_server_error() throws Exception {

		when(trialHistoryService.createTrialHistory(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateTrialHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testTrialHistoryCreate_conflict() throws Exception {

		when(trialHistoryService.createTrialHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.CONFLICT),
						ExceptionMessageConstants.FAILED_TO_CREATE_TRIAL_HISTORY,
						ExceptionMessageConstants.FAILED_TO_CREATE_TRIAL_HISTORY));

		MockHttpServletRequestBuilder mockRequest = post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateTrialHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isConflict())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_CREATE_TRIAL_HISTORY));

	}

	// Test Case for Update
	@Test
	void testTrialHistoryUpdate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Updated Success");

		when(trialHistoryService.updateTrialHistory(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new TrialHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Updated Success"));

	}

	@Test
	void testTrialHistoryUpdate_internal_server_error() throws Exception {

		when(trialHistoryService.updateTrialHistory(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new TrialHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testTrialHistoryUpdate_badRequest() throws Exception {

		when(trialHistoryService.updateTrialHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_UPDATE_TRIAL_HISTORY,
						ExceptionMessageConstants.FAILED_TO_UPDATE_TRIAL_HISTORY));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new TrialHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_UPDATE_TRIAL_HISTORY));

	}

	@Test
	void testTrialHistoryUpdate_notFound() throws Exception {

		when(trialHistoryService.updateTrialHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new TrialHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED));

	}

	// Test Case For TrialHistory Delete

	@Test
	void testTrialHistoryDelete_Basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Removed Success");

		when(trialHistoryService.deleteTrialHistory(Mockito.any())).thenReturn(dto);

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
	void testTrialHistoryDelete_internal_server_error() throws Exception {

		when(trialHistoryService.deleteTrialHistory(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
				ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new TrialHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testTrialHistoryDelete_badRequest() throws Exception {

		when(trialHistoryService.deleteTrialHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_DELETE_TRIAL_HISTORY,
						ExceptionMessageConstants.FAILED_TO_DELETE_TRIAL_HISTORY));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new TrialHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_DELETE_TRIAL_HISTORY));

	}

	@Test
	void testTrialHistoryDelete_notFound() throws Exception {

		when(trialHistoryService.deleteTrialHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new TrialHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_TRIAL_HISTORY_ID_IS_REQUIRED));

	}

	// Test Case for TrialHistory Search Details

	@Test
	void testGetSearchTrialHistorys_basic() throws Exception {

		TrialHistory newTrialHistory = new TrialHistory();
		newTrialHistory.setId(1L);
		newTrialHistory.setInstitutionId(2L);

		Page<TrialHistory> page = new PageImpl<>(Collections.singletonList(newTrialHistory));

		when(this.trialHistoryService.search(Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(page);

		this.mockMvc.perform(get(uriToFetchList)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		// .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].['firstName']").exists())
		// .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].['firstName']").value("John123"));

	}

	@Test
	void testGetSearchTrialHistorys_internalservererror() throws Exception {

		when(trialHistoryService.search(Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any())).thenThrow(
						new BusinessException(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
								ExceptionMessageConstants.INTERNAL_SERVER_ERROR,
								ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = get(uriToFetchList)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new TrialHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

}

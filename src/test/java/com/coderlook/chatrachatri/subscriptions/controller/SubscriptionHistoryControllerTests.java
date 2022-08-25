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

import com.coderlook.chatrachatri.subscriptions.dto.CreateSubscriptionHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.SubscriptionHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.SubscriptionHistory;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.SubscriptionHistoryMapper;
import com.coderlook.chatrachatri.subscriptions.service.SubscriptionHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class SubscriptionHistoryControllerTests {

	@InjectMocks
	public SubscriptionHistoryController subscriptionHistoryController;

	@Mock
	private SubscriptionHistoryService subscriptionHistoryService;

	@Mock
	private SubscriptionHistoryMapper subscriptionHistoryMapper;

	@Autowired
	private MockMvc mockMvc;

	private static ObjectMapper mapper = new ObjectMapper();

	private String uriToFetchById = "/api/subscription-history/{id}";
	private String uriToCreate = "/api/subscription-history/create";
	private String uriToUpdate = "/api/subscription-history/update";
	private String uriToDelete = "/api/subscription-history/delete/{id}";
	private String uriToFetchList = "/api/subscription-history/search";

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(subscriptionHistoryController).build();

	}

	// Test Case for SubscriptionHistory Details

	@Test
	void testGetSubscriptionHistoryDetails_basic() throws Exception {

		SubscriptionHistoryDto subscriptionHistoryDto = new SubscriptionHistoryDto();
		subscriptionHistoryDto.setId(1L);
		subscriptionHistoryDto.setNoOfStudent(150);

		when(this.subscriptionHistoryService.getSubscriptionHistoryById(Mockito.anyLong())).thenReturn(
				new SubscriptionHistory());

		when(this.subscriptionHistoryMapper.subscriptionHistoryToSubscriptionHistoryDto(
				this.subscriptionHistoryService.getSubscriptionHistoryById(Mockito.anyLong()))).thenReturn(
						subscriptionHistoryDto);

		this.mockMvc.perform(get(uriToFetchById, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.noOfStudent").value(150))
				.andReturn();

	}

	@Test
	void testGetSubscriptionHistory_internal_server_error() throws Exception {

		when(subscriptionHistoryService.getSubscriptionHistoryById(Mockito.any())).thenThrow(new APIException(
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
	void testGetSubscriptionHistory_notFound() throws Exception {

		when(this.subscriptionHistoryService.getSubscriptionHistoryById(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED));

		this.mockMvc.perform(get(uriToFetchById, 1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED));

	}

	// Test Case for SubscriptionHistory Create

	@Test
	void testSubscriptionHistoryCreate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Created Success");

		when(subscriptionHistoryService.createSubscriptionHistory(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateSubscriptionHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Created Success"));

	}

	@Test
	void testSubscriptionHistoryCreate_internal_server_error() throws Exception {

		when(subscriptionHistoryService.createSubscriptionHistory(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateSubscriptionHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testSubscriptionHistoryCreate_conflict() throws Exception {

		when(subscriptionHistoryService.createSubscriptionHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.CONFLICT),
						ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION_HISTORY,
						ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION_HISTORY));

		MockHttpServletRequestBuilder mockRequest = post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateSubscriptionHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isConflict())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION_HISTORY));

	}

	// Test Case for Update
	@Test
	void testSubscriptionHistoryUpdate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Updated Success");

		when(subscriptionHistoryService.updateSubscriptionHistory(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Updated Success"));

	}

	@Test
	void testSubscriptionHistoryUpdate_internal_server_error() throws Exception {

		when(subscriptionHistoryService.updateSubscriptionHistory(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testSubscriptionHistoryUpdate_badRequest() throws Exception {

		when(subscriptionHistoryService.updateSubscriptionHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION_HISTORY,
						ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION_HISTORY));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION_HISTORY));

	}

	@Test
	void testSubscriptionHistoryUpdate_notFound() throws Exception {

		when(subscriptionHistoryService.updateSubscriptionHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED));

	}

	// Test Case For SubscriptionHistory Delete

	@Test
	void testSubscriptionHistoryDelete_Basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Removed Success");

		when(subscriptionHistoryService.deleteSubscriptionHistory(Mockito.any())).thenReturn(dto);

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
	void testSubscriptionHistoryDelete_internal_server_error() throws Exception {

		when(subscriptionHistoryService.deleteSubscriptionHistory(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
				ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testSubscriptionHistoryDelete_badRequest() throws Exception {

		when(subscriptionHistoryService.deleteSubscriptionHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION_HISTORY,
						ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION_HISTORY));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION_HISTORY));

	}

	@Test
	void testSubscriptionHistoryDelete_notFound() throws Exception {

		when(subscriptionHistoryService.deleteSubscriptionHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_HISTORY_ID_IS_REQUIRED));

	}

	// Test Case for SubscriptionHistory Search Details

	@Test
	void testGetSearchSubscriptionHistorys_basic() throws Exception {

		SubscriptionHistory newSubscriptionHistory = new SubscriptionHistory();
		newSubscriptionHistory.setId(1L);
		newSubscriptionHistory.setNoOfStudent(100);

		Page<SubscriptionHistory> page = new PageImpl<>(Collections.singletonList(newSubscriptionHistory));

		when(this.subscriptionHistoryService.search(Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(page);

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
	void testGetSearchSubscriptionHistorys_internalservererror() throws Exception {

		when(subscriptionHistoryService.search(Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenThrow(
						new BusinessException(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
								ExceptionMessageConstants.INTERNAL_SERVER_ERROR,
								ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = get(uriToFetchList)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

}

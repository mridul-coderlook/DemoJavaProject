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

import com.coderlook.chatrachatri.subscriptions.dto.CreateSubscriptionDto;
import com.coderlook.chatrachatri.subscriptions.dto.SubscriptionDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.Subscription;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.SubscriptionMapper;
import com.coderlook.chatrachatri.subscriptions.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class SubscriptionControllerTests {

	@InjectMocks
	public SubscriptionController subscriptionController;

	@Mock
	private SubscriptionService subscriptionService;

	@Mock
	private SubscriptionMapper subscriptionMapper;

	@Autowired
	private MockMvc mockMvc;

	private static ObjectMapper mapper = new ObjectMapper();

	private String uriToFetchById = "/api/subscription/{id}";
	private String uriToCreate = "/api/subscription/create";
	private String uriToUpdate = "/api/subscription/update";
	private String uriToDelete = "/api/subscription/delete/{id}";
	private String uriToFetchList = "/api/subscription/search";

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();

	}

	// Test Case for Subscription Details

	@Test
	void testGetSubscriptionDetails_basic() throws Exception {

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto.setId(1L);
		subscriptionDto.setTotalPrice(200.00);

		when(this.subscriptionService.getSubscriptionById(Mockito.anyLong())).thenReturn(
				new Subscription());

		when(this.subscriptionMapper.subscriptionToSubscriptionDto(
				this.subscriptionService.getSubscriptionById(Mockito.anyLong()))).thenReturn(
						subscriptionDto);

		this.mockMvc.perform(get(uriToFetchById, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.totalPrice").value(200.00))
				.andReturn();

	}

	@Test
	void testGetSubscription_internal_server_error() throws Exception {

		when(subscriptionService.getSubscriptionById(Mockito.any())).thenThrow(new APIException(
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
	void testGetSubscription_notFound() throws Exception {

		when(this.subscriptionService.getSubscriptionById(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED));

		this.mockMvc.perform(get(uriToFetchById, 1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED));

	}

	// Test Case for Subscription Create

	@Test
	void testSubscriptionCreate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Created Success");

		when(subscriptionService.createSubscription(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateSubscriptionDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Created Success"));

	}

	@Test
	void testSubscriptionCreate_internal_server_error() throws Exception {

		when(subscriptionService.createSubscription(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateSubscriptionDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testSubscriptionCreate_conflict() throws Exception {

		when(subscriptionService.createSubscription(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.CONFLICT),
						ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION,
						ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION));

		MockHttpServletRequestBuilder mockRequest = post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateSubscriptionDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isConflict())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_CREATE_SUBSCRIPTION));

	}

	// Test Case for Update
	@Test
	void testSubscriptionUpdate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Updated Success");

		when(subscriptionService.updateSubscription(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Updated Success"));

	}

	@Test
	void testSubscriptionUpdate_internal_server_error() throws Exception {

		when(subscriptionService.updateSubscription(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testSubscriptionUpdate_badRequest() throws Exception {

		when(subscriptionService.updateSubscription(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION,
						ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_UPDATE_SUBSCRIPTION));

	}

	@Test
	void testSubscriptionUpdate_notFound() throws Exception {

		when(subscriptionService.updateSubscription(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED));

	}

	// Test Case For Subscription Delete

	@Test
	void testSubscriptionDelete_Basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Removed Success");

		when(subscriptionService.deleteSubscription(Mockito.any())).thenReturn(dto);

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
	void testSubscriptionDelete_internal_server_error() throws Exception {

		when(subscriptionService.deleteSubscription(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
				ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testSubscriptionDelete_badRequest() throws Exception {

		when(subscriptionService.deleteSubscription(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION,
						ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_DELETE_SUBSCRIPTION));

	}

	@Test
	void testSubscriptionDelete_notFound() throws Exception {

		when(subscriptionService.deleteSubscription(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_SUBSCRIPTION_ID_IS_REQUIRED));

	}

	// Test Case for Subscription Search Details

	@Test
	void testGetSearchSubscriptions_basic() throws Exception {

		Subscription newSubscription = new Subscription();
		newSubscription.setId(1L);
		newSubscription.setTotalPrice(200.00);

		Page<Subscription> page = new PageImpl<>(Collections.singletonList(newSubscription));

		when(this.subscriptionService.search(Mockito.any(),
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
	void testGetSearchSubscriptions_internalservererror() throws Exception {

		when(subscriptionService.search(Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any())).thenThrow(
						new BusinessException(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
								ExceptionMessageConstants.INTERNAL_SERVER_ERROR,
								ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = get(uriToFetchList)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new SubscriptionDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

}

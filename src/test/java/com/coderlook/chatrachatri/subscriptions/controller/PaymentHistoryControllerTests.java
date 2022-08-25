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

import com.coderlook.chatrachatri.subscriptions.dto.CreatePaymentHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.PaymentHistoryDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.PaymentHistory;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.PaymentHistoryMapper;
import com.coderlook.chatrachatri.subscriptions.service.PaymentHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentHistoryControllerTests {

	@InjectMocks
	public PaymentHistoryController paymentHistoryController;

	@Mock
	private PaymentHistoryService paymentHistoryService;

	@Mock
	private PaymentHistoryMapper paymentHistoryMapper;

	@Autowired
	private MockMvc mockMvc;

	private static ObjectMapper mapper = new ObjectMapper();

	private String uriToFetchById = "/api/payment-history/{id}";
	private String uriToCreate = "/api/payment-history/create";
	private String uriToUpdate = "/api/payment-history/update";
	private String uriToDelete = "/api/payment-history/delete/{id}";
	private String uriToFetchList = "/api/payment-history/search";

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(paymentHistoryController).build();

	}

	// Test Case for PaymentHistory Details

	@Test
	void testGetPaymentHistoryDetails_basic() throws Exception {

		PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto();
		paymentHistoryDto.setId(1L);
		paymentHistoryDto.setPaymentStatus("TEST SUCCESS");

		when(this.paymentHistoryService.getPaymentHistoryById(Mockito.anyLong())).thenReturn(
				new PaymentHistory());

		when(this.paymentHistoryMapper.paymentHistoryToPaymentHistoryDto(
				this.paymentHistoryService.getPaymentHistoryById(Mockito.anyLong()))).thenReturn(
						paymentHistoryDto);

		this.mockMvc.perform(get(uriToFetchById, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.paymentStatus").value("TEST SUCCESS"))
				.andReturn();

	}

	@Test
	void testGetPaymentHistory_internal_server_error() throws Exception {

		when(paymentHistoryService.getPaymentHistoryById(Mockito.any())).thenThrow(new APIException(
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
	void testGetPaymentHistory_notFound() throws Exception {

		when(this.paymentHistoryService.getPaymentHistoryById(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED));

		this.mockMvc.perform(get(uriToFetchById, 1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED));

	}

	// Test Case for PaymentHistory Create

	@Test
	void testPaymentHistoryCreate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Created Success");

		when(paymentHistoryService.createPaymentHistory(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreatePaymentHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Created Success"));

	}

	@Test
	void testPaymentHistoryCreate_internal_server_error() throws Exception {

		when(paymentHistoryService.createPaymentHistory(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreatePaymentHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testPaymentHistoryCreate_conflict() throws Exception {

		when(paymentHistoryService.createPaymentHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.CONFLICT),
						ExceptionMessageConstants.FAILED_TO_CREATE_PAYMENT_HISTORY,
						ExceptionMessageConstants.FAILED_TO_CREATE_PAYMENT_HISTORY));

		MockHttpServletRequestBuilder mockRequest = post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreatePaymentHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isConflict())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_CREATE_PAYMENT_HISTORY));

	}

	// Test Case for Update
	@Test
	void testPaymentHistoryUpdate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Updated Success");

		when(paymentHistoryService.updatePaymentHistory(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PaymentHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Updated Success"));

	}

	@Test
	void testPaymentHistoryUpdate_internal_server_error() throws Exception {

		when(paymentHistoryService.updatePaymentHistory(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PaymentHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testPaymentHistoryUpdate_badRequest() throws Exception {

		when(paymentHistoryService.updatePaymentHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_UPDATE_PAYMENT_HISTORY,
						ExceptionMessageConstants.FAILED_TO_UPDATE_PAYMENT_HISTORY));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PaymentHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_UPDATE_PAYMENT_HISTORY));

	}

	@Test
	void testPaymentHistoryUpdate_notFound() throws Exception {

		when(paymentHistoryService.updatePaymentHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PaymentHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED));

	}

	// Test Case For PaymentHistory Delete

	@Test
	void testPaymentHistoryDelete_Basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Removed Success");

		when(paymentHistoryService.deletePaymentHistory(Mockito.any())).thenReturn(dto);

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
	void testPaymentHistoryDelete_internal_server_error() throws Exception {

		when(paymentHistoryService.deletePaymentHistory(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
				ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PaymentHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testPaymentHistoryDelete_badRequest() throws Exception {

		when(paymentHistoryService.deletePaymentHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_DELETE_PAYMENT_HISTORY,
						ExceptionMessageConstants.FAILED_TO_DELETE_PAYMENT_HISTORY));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PaymentHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_DELETE_PAYMENT_HISTORY));

	}

	@Test
	void testPaymentHistoryDelete_notFound() throws Exception {

		when(paymentHistoryService.deletePaymentHistory(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PaymentHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_PAYMENT_HISTORY_ID_IS_REQUIRED));

	}

	// Test Case for PaymentHistory Search Details

	@Test
	void testGetSearchPaymentHistorys_basic() throws Exception {

		PaymentHistory newPaymentHistory = new PaymentHistory();
		newPaymentHistory.setId(1L);
		newPaymentHistory.setPaymentType("CASH");
		newPaymentHistory.setPaymentStatus("SUCCESS");

		Page<PaymentHistory> page = new PageImpl<>(Collections.singletonList(newPaymentHistory));

		when(this.paymentHistoryService.search(Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(),
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
	void testGetSearchPaymentHistorys_internalservererror() throws Exception {

		when(paymentHistoryService.search(Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any())).thenThrow(
						new BusinessException(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
								ExceptionMessageConstants.INTERNAL_SERVER_ERROR,
								ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = get(uriToFetchList)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new PaymentHistoryDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

}

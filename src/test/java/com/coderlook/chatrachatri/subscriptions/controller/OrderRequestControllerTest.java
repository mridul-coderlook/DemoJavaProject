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

import com.coderlook.chatrachatri.subscriptions.dto.CreateOrderRequestDto;
import com.coderlook.chatrachatri.subscriptions.dto.OrderRequestDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.OrderRequest;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.OrderRequestMapper;
import com.coderlook.chatrachatri.subscriptions.service.OrderRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderRequestControllerTest {

    @InjectMocks
    public OrderRequestController orderRequestController;

    @Mock
    private OrderRequestService orderRequestService;

    @Mock
    private OrderRequestMapper orderRequestMapper;

    @Autowired
    private MockMvc mockMvc;

    private static ObjectMapper mapper = new ObjectMapper();

    private String uriToFetchById = "/api/order-request/{id}";
    private String uriToCreate = "/api/order-request/create";
    private String uriToUpdate = "/api/order-request/update";
    private String uriToDelete = "/api/order-request/delete/{id}";
    private String uriToFetchList = "/api/order-request/search";

    @BeforeEach
    public void SetupContext() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(orderRequestController).build();

    }

    // Test Case for OrderRequest Details

    @Test
    void testGetOrderRequestDetails_basic() throws Exception {

        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setId(1L);
        orderRequestDto.setCouponCode("FIRSTTEST");

        when(this.orderRequestService.getOrderRequestById(Mockito.anyLong())).thenReturn(
                new OrderRequest());

        when(this.orderRequestMapper.orderRequestToOrderRequestDto(
                this.orderRequestService.getOrderRequestById(Mockito.anyLong()))).thenReturn(
                        orderRequestDto);

        this.mockMvc.perform(get(uriToFetchById, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.couponCode").value("FIRSTTEST"))
                .andReturn();

    }

    @Test
    void testGetOrderRequest_internal_server_error() throws Exception {

        when(orderRequestService.getOrderRequestById(Mockito.any())).thenThrow(new APIException(
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
    void testGetOrderRequest_notFound() throws Exception {

        when(this.orderRequestService.getOrderRequestById(Mockito.any())).thenThrow(
                new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
                        ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED,
                        ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED));

        this.mockMvc.perform(get(uriToFetchById, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED));

    }

    // Test Case for OrderRequest Create

    @Test
    void testOrderRequestCreate_basic() throws Exception {

        SuccessDto dto = new SuccessDto();
        dto.setMessage("Created Success");

        when(orderRequestService.createOrderRequest(Mockito.any())).thenReturn(dto);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new CreateOrderRequestDto()));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Created Success"));

    }

    @Test
    void testOrderRequestCreate_internal_server_error() throws Exception {

        when(orderRequestService.createOrderRequest(Mockito.any())).thenThrow(new APIException(
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new CreateOrderRequestDto()));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

    }

    @Test
    void testOrderRequestCreate_conflict() throws Exception {

        when(orderRequestService.createOrderRequest(Mockito.any())).thenThrow(
                new BusinessException(String.valueOf(HttpStatus.CONFLICT),
                        ExceptionMessageConstants.FAILED_TO_CREATE_ORDER_REQUEST,
                        ExceptionMessageConstants.FAILED_TO_CREATE_ORDER_REQUEST));

        MockHttpServletRequestBuilder mockRequest = post(uriToCreate)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new CreateOrderRequestDto()));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ExceptionMessageConstants.FAILED_TO_CREATE_ORDER_REQUEST));

    }

    // Test Case for Update
    @Test
    void testOrderRequestUpdate_basic() throws Exception {

        SuccessDto dto = new SuccessDto();
        dto.setMessage("Updated Success");

        when(orderRequestService.updateOrderRequest(Mockito.any())).thenReturn(dto);

        MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new OrderRequestDto()));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Updated Success"));

    }

    @Test
    void testOrderRequestUpdate_internal_server_error() throws Exception {

        when(orderRequestService.updateOrderRequest(Mockito.any())).thenThrow(new APIException(
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

        MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new OrderRequestDto()));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

    }

    @Test
    void testOrderRequestUpdate_badRequest() throws Exception {

        when(orderRequestService.updateOrderRequest(Mockito.any())).thenThrow(
                new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                        ExceptionMessageConstants.FAILED_TO_UPDATE_ORDER_REQUEST,
                        ExceptionMessageConstants.FAILED_TO_UPDATE_ORDER_REQUEST));

        MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new OrderRequestDto()));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ExceptionMessageConstants.FAILED_TO_UPDATE_ORDER_REQUEST));

    }

    @Test
    void testOrderRequestUpdate_notFound() throws Exception {

        when(orderRequestService.updateOrderRequest(Mockito.any())).thenThrow(
                new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
                        ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED,
                        ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED));

        MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new OrderRequestDto()));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED));

    }

    // Test Case For OrderRequest Delete

    @Test
    void testOrderRequestDelete_Basic() throws Exception {

        SuccessDto dto = new SuccessDto();
        dto.setMessage("Removed Success");

        when(orderRequestService.deleteOrderRequest(Mockito.any())).thenReturn(dto);

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
    void testOrderRequestDelete_internal_server_error() throws Exception {

        when(orderRequestService.deleteOrderRequest(Mockito.any())).thenThrow(new APIException(
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
                ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

        MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new OrderRequestDto()));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

    }

    @Test
    void testOrderRequestDelete_badRequest() throws Exception {

        when(orderRequestService.deleteOrderRequest(Mockito.any())).thenThrow(
                new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
                        ExceptionMessageConstants.FAILED_TO_DELETE_ORDER_REQUEST,
                        ExceptionMessageConstants.FAILED_TO_DELETE_ORDER_REQUEST));

        MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new OrderRequestDto()));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ExceptionMessageConstants.FAILED_TO_DELETE_ORDER_REQUEST));

    }

    @Test
    void testOrderRequestDelete_notFound() throws Exception {

        when(orderRequestService.deleteOrderRequest(Mockito.any())).thenThrow(
                new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
                        ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED,
                        ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED));

        MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new OrderRequestDto()));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ExceptionMessageConstants.NOT_FOUND_ORDER_REQUEST_ID_IS_REQUIRED));

    }

    // Test Case for OrderRequest Search Details

    @Test
    void testGetSearchOrderRequests_basic() throws Exception {

        OrderRequest newOrderRequest = new OrderRequest();
        newOrderRequest.setId(1L);
        newOrderRequest.setCouponCode("FIRSTTEST");
        newOrderRequest.setDiscount(20.00);

        Page<OrderRequest> page = new PageImpl<>(Collections.singletonList(newOrderRequest));

        when(this.orderRequestService.search(
                Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(page);

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
    void testGetSearchOrderRequests_internalservererror() throws Exception {

        when(orderRequestService.search(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any())).thenThrow(
                        new BusinessException(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
                                ExceptionMessageConstants.INTERNAL_SERVER_ERROR,
                                ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

        MockHttpServletRequestBuilder mockRequest = get(uriToFetchList)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new OrderRequestDto()));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

    }

}

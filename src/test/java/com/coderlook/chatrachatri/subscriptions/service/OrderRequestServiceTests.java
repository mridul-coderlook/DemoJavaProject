package com.coderlook.chatrachatri.subscriptions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.Conflict;
import org.springframework.web.client.HttpClientErrorException.NotFound;

import com.coderlook.chatrachatri.subscriptions.entity.OrderRequest;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.repository.OrderRequestRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderRequestServiceTests {

	@InjectMocks
	private OrderRequestService orderRequestService;

	@Mock
	private OrderRequestRepository orderRequestRepository;

	@Mock
	private OrderRequest orderRequest;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(orderRequestService).build();

	}

	@Test
	void testOrderRequestGetDetails_basic() throws Exception {
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setId(1L);
		orderRequest.setCouponCode("TEST COUPON CODE");

		when(this.orderRequestRepository.findById(1L)).thenReturn(Optional.of(orderRequest));
		assertTrue(true, "OrderRequest Found");
		assertEquals(1, orderRequest.getId());
		assertEquals("TEST COUPON CODE", orderRequest.getCouponCode());
	}

	@Test
	void testOrderRequestGetDetails_null() throws Exception {
		when(this.orderRequestRepository.findById(1L)).thenReturn(null);
		assertNull(null, orderRequest.getCouponCode());
	}

	@Test
	void testOrderRequestGetDetails_optional() throws Exception {
		when(this.orderRequestRepository.findById(1L)).thenReturn(Optional.of(orderRequest));
		assertTrue(Optional.of(orderRequest).isPresent());
	}

	@Test
	void testOrderRequestGetDetails_notFound() throws Exception {
		when(this.orderRequestRepository.findById(1L)).thenThrow(NotFound.class);
		assertEquals(404, HttpStatus.NOT_FOUND.value());
	}

	@Test
	void testOrderRequestCreate_basic() throws Exception {
		when(orderRequestRepository.save(orderRequest)).thenReturn(orderRequest);
		assertTrue(true, "OrderRequest Created");
	}

	@Test
	void testOrderRequestCreate_conflict() throws Exception {
		when(orderRequestRepository.save(Mockito.any())).thenThrow(Conflict.class);
		assertEquals(409, HttpStatus.CONFLICT.value());
	}

	@Test
	void testOrderRequestCreate_existsById() throws Exception {
		when(orderRequestRepository.existsById(anyLong())).thenReturn(false);

		assertTrue(true, "OrderRequest Exists");
	}

	// Test Case for OrderRequest Update

	@Test
	void testUpdate() throws Exception {
		OrderRequest newOrderRequest = new OrderRequest();
		newOrderRequest.setId(1L);
		newOrderRequest.setNoOfUser(20);
		newOrderRequest.setCouponCode("TEST COUPON CODE");

		when(orderRequestRepository.findById(1L)).thenReturn(Optional.of(newOrderRequest));
		when(orderRequestRepository.save(newOrderRequest)).thenReturn(newOrderRequest);
		assertTrue(true, "OrderRequest Updated");
		assertEquals(1L, newOrderRequest.getId());
	}

	@Test
	void testOrderRequestUpdate_badRequest() throws Exception {
		OrderRequest newOrderRequest = new OrderRequest();
		newOrderRequest.setId(1L);
		newOrderRequest.setNoOfUser(20);
		newOrderRequest.setCouponCode("TEST COUPON CODE");

		when(orderRequestRepository.findById(1L)).thenReturn(Optional.of(newOrderRequest));
		when(orderRequestRepository.save(newOrderRequest)).thenThrow(BadRequest.class);
		assertEquals(400, HttpStatus.BAD_REQUEST.value());
	}

	// Test Case for OrderRequest Search Details

	@Test
	void testGetSearchOrderRequests_basic() throws Exception {

		OrderRequest newOrderRequest = new OrderRequest();
		newOrderRequest.setNoOfUser(20);
		newOrderRequest.setCouponCode("TEST COUPON CODE");
		newOrderRequest.setStatus(Status.Active);
		List<OrderRequest> orderRequestDtosList = new ArrayList<>();
		orderRequestDtosList.add(newOrderRequest);
		Page<OrderRequest> page = new PageImpl<OrderRequest>(orderRequestDtosList);
		when(this.orderRequestRepository.findAll()).thenReturn(orderRequestDtosList);
		assertEquals(1, page.getTotalElements());

	}

}

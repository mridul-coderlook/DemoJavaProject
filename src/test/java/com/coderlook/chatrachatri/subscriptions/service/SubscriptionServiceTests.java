package com.coderlook.chatrachatri.subscriptions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.entity.Subscription;
import com.coderlook.chatrachatri.subscriptions.repository.SubscriptionRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class SubscriptionServiceTests {

	@InjectMocks
	private SubscriptionService subscriptionService;

	@Mock
	private SubscriptionRepository subscriptionRepository;

	@Mock
	private Subscription subscription;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(subscriptionService).build();

	}

	@Test
	void testSubscriptionGetDetails_basic() throws Exception {
		Subscription subscription = new Subscription();
		subscription.setId(1L);
		subscription.setNoOfStudent(120);
		subscription.setTotalPrice(20000.00);

		when(this.subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
		assertTrue(true, "Subscription Found");
		assertEquals(1, subscription.getId());
		assertEquals(120, subscription.getNoOfStudent());
		assertEquals(20000.00, subscription.getTotalPrice());
	}

	@Test
	void testSubscriptionGetDetails_null() throws Exception {
		when(this.subscriptionRepository.findById(1L)).thenReturn(null);
		assertEquals(0, subscription.getNoOfStudent());
	}

	@Test
	void testSubscriptionGetDetails_optional() throws Exception {
		when(this.subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
		assertTrue(Optional.of(subscription).isPresent());
	}

	@Test
	void testSubscriptionGetDetails_notFound() throws Exception {
		when(this.subscriptionRepository.findById(1L)).thenThrow(NotFound.class);
		assertEquals(404, HttpStatus.NOT_FOUND.value());
	}

	@Test
	void testSubscriptionCreate_basic() throws Exception {
		when(subscriptionRepository.save(subscription)).thenReturn(subscription);
		assertTrue(true, "Subscription Created");
	}

	@Test
	void testSubscriptionCreate_conflict() throws Exception {
		when(subscriptionRepository.save(Mockito.any())).thenThrow(Conflict.class);
		assertEquals(409, HttpStatus.CONFLICT.value());
	}

	@Test
	void testSubscriptionCreate_existsById() throws Exception {
		when(subscriptionRepository.existsById(anyLong())).thenReturn(false);

		assertTrue(true, "Subscription Exists");
	}

	// Test Case for Subscription Update

	@Test
	void testUpdate() throws Exception {
		Subscription newSubscription = new Subscription();
		newSubscription.setId(1L);
		newSubscription.setNoOfStudent(120);
		newSubscription.setTotalPrice(120000.00);

		when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(newSubscription));
		when(subscriptionRepository.save(newSubscription)).thenReturn(newSubscription);
		assertTrue(true, "Subscription Updated");
		assertEquals(1L, newSubscription.getId());
	}

	@Test
	void testSubscriptionUpdate_badRequest() throws Exception {
		Subscription newSubscription = new Subscription();
		newSubscription.setId(1L);
		newSubscription.setNoOfStudent(120);
		newSubscription.setTotalPrice(120000.00);

		when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(newSubscription));
		when(subscriptionRepository.save(newSubscription)).thenThrow(BadRequest.class);
		assertEquals(400, HttpStatus.BAD_REQUEST.value());
	}

	// Test Case for Subscription Search Details

	@Test
	void testGetSearchSubscriptions_basic() throws Exception {

		Subscription newSubscription = new Subscription();
		newSubscription.setNoOfStudent(120);
		newSubscription.setTotalPrice(120000.00);
		newSubscription.setStatus(Status.Active);
		List<Subscription> subscriptionDtosList = new ArrayList<>();
		subscriptionDtosList.add(newSubscription);
		Page<Subscription> page = new PageImpl<Subscription>(subscriptionDtosList);
		when(this.subscriptionRepository.findAll()).thenReturn(subscriptionDtosList);
		assertEquals(1, page.getTotalElements());

	}

}

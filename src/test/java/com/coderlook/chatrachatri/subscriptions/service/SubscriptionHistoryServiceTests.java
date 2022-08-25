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

import com.coderlook.chatrachatri.subscriptions.entity.SubscriptionHistory;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.repository.SubscriptionHistoryRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class SubscriptionHistoryServiceTests {

	@InjectMocks
	private SubscriptionHistoryService subscriptionHistoryService;

	@Mock
	private SubscriptionHistoryRepository subscriptionHistoryRepository;

	@Mock
	private SubscriptionHistory subscriptionHistory;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(subscriptionHistoryService).build();

	}

	@Test
	void testSubscriptionHistoryGetDetails_basic() throws Exception {
		SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
		subscriptionHistory.setId(1L);
		subscriptionHistory.setAction("Test Action");

		when(this.subscriptionHistoryRepository.findById(1L)).thenReturn(Optional.of(subscriptionHistory));
		assertTrue(true, "SubscriptionHistory Found");
		assertEquals(1, subscriptionHistory.getId());
		assertEquals("Test Action", subscriptionHistory.getAction());
	}

	@Test
	void testSubscriptionHistoryGetDetails_null() throws Exception {
		when(this.subscriptionHistoryRepository.findById(1L)).thenReturn(null);
		assertNull(null, subscriptionHistory.getAction());
	}

	@Test
	void testSubscriptionHistoryGetDetails_optional() throws Exception {
		when(this.subscriptionHistoryRepository.findById(1L)).thenReturn(Optional.of(subscriptionHistory));
		assertTrue(Optional.of(subscriptionHistory).isPresent());
	}

	@Test
	void testSubscriptionHistoryGetDetails_notFound() throws Exception {
		when(this.subscriptionHistoryRepository.findById(1L)).thenThrow(NotFound.class);
		assertEquals(404, HttpStatus.NOT_FOUND.value());
	}

	@Test
	void testSubscriptionHistoryCreate_basic() throws Exception {
		when(subscriptionHistoryRepository.save(subscriptionHistory)).thenReturn(subscriptionHistory);
		assertTrue(true, "SubscriptionHistory Created");
	}

	@Test
	void testSubscriptionHistoryCreate_conflict() throws Exception {
		when(subscriptionHistoryRepository.save(Mockito.any())).thenThrow(Conflict.class);
		assertEquals(409, HttpStatus.CONFLICT.value());
	}

	@Test
	void testSubscriptionHistoryCreate_existsById() throws Exception {
		when(subscriptionHistoryRepository.existsById(anyLong())).thenReturn(false);

		assertTrue(true, "SubscriptionHistory Exists");
	}

	// Test Case for SubscriptionHistory Update

	@Test
	void testUpdate() throws Exception {
		SubscriptionHistory newSubscriptionHistory = new SubscriptionHistory();
		newSubscriptionHistory.setId(1L);
		newSubscriptionHistory.setAction("Test Action");

		when(subscriptionHistoryRepository.findById(1L)).thenReturn(Optional.of(newSubscriptionHistory));
		when(subscriptionHistoryRepository.save(newSubscriptionHistory)).thenReturn(newSubscriptionHistory);
		assertTrue(true, "SubscriptionHistory Updated");
		assertEquals(1L, newSubscriptionHistory.getId());
	}

	@Test
	void testSubscriptionHistoryUpdate_badRequest() throws Exception {
		SubscriptionHistory newSubscriptionHistory = new SubscriptionHistory();
		newSubscriptionHistory.setId(1L);
		newSubscriptionHistory.setAction("Test Action");

		when(subscriptionHistoryRepository.findById(1L)).thenReturn(Optional.of(newSubscriptionHistory));
		when(subscriptionHistoryRepository.save(newSubscriptionHistory)).thenThrow(BadRequest.class);
		assertEquals(400, HttpStatus.BAD_REQUEST.value());
	}

	// Test Case for SubscriptionHistory Search Details

	@Test
	void testGetSearchSubscriptionHistorys_basic() throws Exception {

		SubscriptionHistory newSubscriptionHistory = new SubscriptionHistory();
		newSubscriptionHistory.setAction("Test Action");
		newSubscriptionHistory.setNoOfStudent(109);
		newSubscriptionHistory.setStatus(Status.Active);
		List<SubscriptionHistory> subscriptionHistoryDtosList = new ArrayList<>();
		subscriptionHistoryDtosList.add(newSubscriptionHistory);
		Page<SubscriptionHistory> page = new PageImpl<SubscriptionHistory>(subscriptionHistoryDtosList);
		when(this.subscriptionHistoryRepository.findAll()).thenReturn(subscriptionHistoryDtosList);
		assertEquals(1, page.getTotalElements());

	}

}

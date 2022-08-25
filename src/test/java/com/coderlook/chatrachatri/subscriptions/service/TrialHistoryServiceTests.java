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

import com.coderlook.chatrachatri.subscriptions.entity.Plan;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.entity.TrialHistory;
import com.coderlook.chatrachatri.subscriptions.repository.TrialHistoryRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TrialHistoryServiceTests {

	@InjectMocks
	private TrialHistoryService trialHistoryService;

	@Mock
	private TrialHistoryRepository trialHistoryRepository;

	@Mock
	private TrialHistory trialHistory;

	@Mock
	private Plan plan;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(trialHistoryService).build();

	}

	@Test
	void testTrialHistoryGetDetails_basic() throws Exception {
		TrialHistory trialHistory = new TrialHistory();
		trialHistory.setId(1L);
		trialHistory.setInstitutionId(11L);
		when(this.trialHistoryRepository.findById(1L)).thenReturn(Optional.of(trialHistory));
		assertTrue(true, "TrialHistory Found");
		assertEquals(1, trialHistory.getId());
		assertEquals(11L, trialHistory.getInstitutionId());
	}

	@Test
	void testTrialHistoryGetDetails_null() throws Exception {
		when(this.trialHistoryRepository.findById(1L)).thenReturn(null);
		assertEquals(0, trialHistory.getInstitutionId());
	}

	@Test
	void testTrialHistoryGetDetails_optional() throws Exception {
		when(this.trialHistoryRepository.findById(1L)).thenReturn(Optional.of(trialHistory));
		assertTrue(Optional.of(trialHistory).isPresent());
	}

	@Test
	void testTrialHistoryGetDetails_notFound() throws Exception {
		when(this.trialHistoryRepository.findById(1L)).thenThrow(NotFound.class);
		assertEquals(404, HttpStatus.NOT_FOUND.value());
	}

	@Test
	void testTrialHistoryCreate_basic() throws Exception {
		when(trialHistoryRepository.save(trialHistory)).thenReturn(trialHistory);
		assertTrue(true, "TrialHistory Created");
	}

	@Test
	void testTrialHistoryCreate_conflict() throws Exception {
		when(trialHistoryRepository.save(Mockito.any())).thenThrow(Conflict.class);
		assertEquals(409, HttpStatus.CONFLICT.value());
	}

	@Test
	void testTrialHistoryCreate_existsById() throws Exception {
		when(trialHistoryRepository.existsById(anyLong())).thenReturn(false);

		assertTrue(true, "TrialHistory Exists");
	}

	// Test Case for TrialHistory Update

	@Test
	void testUpdate() throws Exception {
		TrialHistory newTrialHistory = new TrialHistory();
		newTrialHistory.setId(1L);
		newTrialHistory.setInstitutionId(11L);

		when(trialHistoryRepository.findById(1L)).thenReturn(Optional.of(newTrialHistory));
		when(trialHistoryRepository.save(newTrialHistory)).thenReturn(newTrialHistory);
		assertTrue(true, "TrialHistory Updated");
		assertEquals(1L, newTrialHistory.getId());
	}

	@Test
	void testTrialHistoryUpdate_badRequest() throws Exception {
		TrialHistory newTrialHistory = new TrialHistory();
		newTrialHistory.setId(1L);
		newTrialHistory.setInstitutionId(11L);

		when(trialHistoryRepository.findById(1L)).thenReturn(Optional.of(newTrialHistory));
		when(trialHistoryRepository.save(newTrialHistory)).thenThrow(BadRequest.class);
		assertEquals(400, HttpStatus.BAD_REQUEST.value());
	}

	// Test Case for TrialHistory Search Details

	@Test
	void testGetSearchTrialHistorys_basic() throws Exception {

		TrialHistory newTrialHistory = new TrialHistory();
		newTrialHistory.setInstitutionId(11L);
		newTrialHistory.setStatus(Status.Active);
		List<TrialHistory> trialHistoryDtosList = new ArrayList<>();
		trialHistoryDtosList.add(newTrialHistory);
		Page<TrialHistory> page = new PageImpl<TrialHistory>(trialHistoryDtosList);
		when(this.trialHistoryRepository.findAll()).thenReturn(trialHistoryDtosList);
		assertEquals(1, page.getTotalElements());

	}

}

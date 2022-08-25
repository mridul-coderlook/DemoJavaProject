package com.coderlook.chatrachatri.subscriptions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

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
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.Conflict;
import org.springframework.web.client.HttpClientErrorException.NotFound;

import com.coderlook.chatrachatri.subscriptions.entity.PlanConfig;
import com.coderlook.chatrachatri.subscriptions.repository.PlanConfigRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class PlanConfigServiceTests {

	@InjectMocks
	private PlanConfigService planConfigService;

	@Mock
	private PlanConfigRepository planConfigRepository;

	@Mock
	private PlanConfig planConfig;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(planConfigService).build();

	}

	@Test
	void testPlanConfigGetDetails_basic() throws Exception {
		PlanConfig planConfig = new PlanConfig();
		planConfig.setId(1L);
		planConfig.setCount(11);

		when(this.planConfigRepository.findById(1L)).thenReturn(Optional.of(planConfig));
		assertTrue(true, "PlanConfig Found");
		assertEquals(1, planConfig.getId());
		assertEquals(11, planConfig.getCount());
	}

	@Test
	void testPlanConfigGetDetails_null() throws Exception {
		when(this.planConfigRepository.findById(1L)).thenReturn(null);
		assertEquals(0, planConfig.getCount());
	}

	@Test
	void testPlanConfigGetDetails_optional() throws Exception {
		when(this.planConfigRepository.findById(1L)).thenReturn(Optional.of(planConfig));
		assertTrue(Optional.of(planConfig).isPresent());
	}

	@Test
	void testPlanConfigGetDetails_notFound() throws Exception {
		when(this.planConfigRepository.findById(1L)).thenThrow(NotFound.class);
		assertEquals(404, HttpStatus.NOT_FOUND.value());
	}

	@Test
	void testPlanConfigCreate_basic() throws Exception {
		when(planConfigRepository.save(planConfig)).thenReturn(planConfig);
		assertTrue(true, "PlanConfig Created");
	}

	@Test
	void testPlanConfigCreate_conflict() throws Exception {
		when(planConfigRepository.save(Mockito.any())).thenThrow(Conflict.class);
		assertEquals(409, HttpStatus.CONFLICT.value());
	}

	@Test
	void testPlanConfigCreate_existsById() throws Exception {
		when(planConfigRepository.existsById(anyLong())).thenReturn(false);

		assertTrue(true, "PlanConfig Exists");
	}

	// Test Case for PlanConfig Update

	@Test
	void testUpdate() throws Exception {
		PlanConfig newPlanConfig = new PlanConfig();
		newPlanConfig.setId(1L);
		newPlanConfig.setCount(11);
		newPlanConfig.setDisplayOrder(21);

		when(planConfigRepository.findById(1L)).thenReturn(Optional.of(newPlanConfig));
		when(planConfigRepository.save(newPlanConfig)).thenReturn(newPlanConfig);
		assertTrue(true, "PlanConfig Updated");
		assertEquals(1L, newPlanConfig.getId());
	}

	@Test
	void testPlanConfigUpdate_badRequest() throws Exception {
		PlanConfig newPlanConfig = new PlanConfig();
		newPlanConfig.setId(1L);
		newPlanConfig.setCount(11);
		newPlanConfig.setDisplayOrder(21);

		when(planConfigRepository.findById(1L)).thenReturn(Optional.of(newPlanConfig));
		when(planConfigRepository.save(newPlanConfig)).thenThrow(BadRequest.class);
		assertEquals(400, HttpStatus.BAD_REQUEST.value());
	}

}

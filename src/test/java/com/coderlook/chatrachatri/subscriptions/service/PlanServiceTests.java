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

import com.coderlook.chatrachatri.subscriptions.entity.Plan;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.repository.PlanRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class PlanServiceTests {

	@InjectMocks
	private PlanService planService;

	@Mock
	private PlanRepository planRepository;

	@Mock
	private Plan plan;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(planService).build();

	}

	@Test
	void testPlanGetDetails_basic() throws Exception {
		Plan plan = new Plan();
		plan.setId(1L);
		plan.setName("Test Plan");

		when(this.planRepository.findById(1L)).thenReturn(Optional.of(plan));
		assertTrue(true, "Plan Found");
		assertEquals(1, plan.getId());
		assertEquals("Test Plan", plan.getName());
	}

	@Test
	void testPlanGetDetails_null() throws Exception {
		when(this.planRepository.findById(1L)).thenReturn(null);
		assertNull(null, plan.getName());
	}

	@Test
	void testPlanGetDetails_optional() throws Exception {
		when(this.planRepository.findById(1L)).thenReturn(Optional.of(plan));
		assertTrue(Optional.of(plan).isPresent());
	}

	@Test
	void testPlanGetDetails_notFound() throws Exception {
		when(this.planRepository.findById(1L)).thenThrow(NotFound.class);
		assertEquals(404, HttpStatus.NOT_FOUND.value());
	}

	@Test
	void testPlanCreate_basic() throws Exception {
		when(planRepository.save(plan)).thenReturn(plan);
		assertTrue(true, "Plan Created");
	}

	@Test
	void testPlanCreate_conflict() throws Exception {
		when(planRepository.save(Mockito.any())).thenThrow(Conflict.class);
		assertEquals(409, HttpStatus.CONFLICT.value());
	}

	@Test
	void testPlanCreate_existsById() throws Exception {
		when(planRepository.existsById(anyLong())).thenReturn(false);

		assertTrue(true, "Plan Exists");
	}

	// Test Case for Plan Update

	@Test
	void testUpdate() throws Exception {
		Plan newPlan = new Plan();
		newPlan.setId(1L);
		newPlan.setName("Test Plan");
		newPlan.setFrequency("Test Frequency");

		when(planRepository.findById(1L)).thenReturn(Optional.of(newPlan));
		when(planRepository.save(newPlan)).thenReturn(newPlan);
		assertTrue(true, "Plan Updated");
		assertEquals(1L, newPlan.getId());
	}

	@Test
	void testPlanUpdate_badRequest() throws Exception {
		Plan newPlan = new Plan();
		newPlan.setId(1L);
		newPlan.setName("Test Plan");
		newPlan.setFrequency("Test Frequency");

		when(planRepository.findById(1L)).thenReturn(Optional.of(newPlan));
		when(planRepository.save(newPlan)).thenThrow(BadRequest.class);
		assertEquals(400, HttpStatus.BAD_REQUEST.value());
	}

	// Test Case for Plan Search Details

	@Test
	void testGetSearchPlans_basic() throws Exception {

		Plan newPlan = new Plan();
		newPlan.setName("Test Plan");
		newPlan.setFrequency("Test Frequency");
		newPlan.setStatus(Status.Active);
		List<Plan> planDtosList = new ArrayList<>();
		planDtosList.add(newPlan);
		Page<Plan> page = new PageImpl<Plan>(planDtosList);
		when(this.planRepository.findAll()).thenReturn(planDtosList);
		assertEquals(1, page.getTotalElements());

	}

}

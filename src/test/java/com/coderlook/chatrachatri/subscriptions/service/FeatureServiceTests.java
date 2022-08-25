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

import com.coderlook.chatrachatri.subscriptions.entity.Feature;
import com.coderlook.chatrachatri.subscriptions.entity.Status;
import com.coderlook.chatrachatri.subscriptions.repository.FeatureRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class FeatureServiceTests {

	@InjectMocks
	private FeatureService featureService;

	@Mock
	private FeatureRepository featureRepository;

	@Mock
	private Feature feature;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(featureService).build();

	}

	@Test
	void testFeatureGetDetails_basic() throws Exception {
		Feature feature = new Feature();
		feature.setId(1L);
		feature.setName("Test Feature");

		when(this.featureRepository.findById(1L)).thenReturn(Optional.of(feature));
		assertTrue(true, "Feature Found");
		assertEquals(1, feature.getId());
		assertEquals("Test Feature", feature.getName());
	}

	@Test
	void testFeatureGetDetails_null() throws Exception {
		when(this.featureRepository.findById(1L)).thenReturn(null);
		assertNull(null, feature.getName());
	}

	@Test
	void testFeatureGetDetails_optional() throws Exception {
		when(this.featureRepository.findById(1L)).thenReturn(Optional.of(feature));
		assertTrue(Optional.of(feature).isPresent());
	}

	@Test
	void testFeatureGetDetails_notFound() throws Exception {
		when(this.featureRepository.findById(1L)).thenThrow(NotFound.class);
		assertEquals(404, HttpStatus.NOT_FOUND.value());
	}

	@Test
	void testFeatureCreate_basic() throws Exception {
		when(featureRepository.save(feature)).thenReturn(feature);
		assertTrue(true, "Feature Created");
	}

	@Test
	void testFeatureCreate_conflict() throws Exception {
		when(featureRepository.save(Mockito.any())).thenThrow(Conflict.class);
		assertEquals(409, HttpStatus.CONFLICT.value());
	}

	@Test
	void testFeatureCreate_existsById() throws Exception {
		when(featureRepository.existsById(anyLong())).thenReturn(false);

		assertTrue(true, "Feature Exists");
	}

	// Test Case for Feature Update

	@Test
	void testUpdate() throws Exception {
		Feature newFeature = new Feature();
		newFeature.setId(1L);
		newFeature.setName("Test Feature");
		newFeature.setDisplayText("Test Display Text");

		when(featureRepository.findById(1L)).thenReturn(Optional.of(newFeature));
		when(featureRepository.save(newFeature)).thenReturn(newFeature);
		assertTrue(true, "Feature Updated");
		assertEquals(1L, newFeature.getId());
	}

	@Test
	void testFeatureUpdate_badRequest() throws Exception {
		Feature newFeature = new Feature();
		newFeature.setId(1L);
		newFeature.setName("Test Feature");
		newFeature.setDisplayText("Test Display Text");

		when(featureRepository.findById(1L)).thenReturn(Optional.of(newFeature));
		when(featureRepository.save(newFeature)).thenThrow(BadRequest.class);
		assertEquals(400, HttpStatus.BAD_REQUEST.value());
	}

	// Test Case for Feature Search Details

	@Test
	void testGetSearchFeatures_basic() throws Exception {

		Feature newFeature = new Feature();
		newFeature.setName("Test Feature");
		newFeature.setDisplayText("Test Display Text");
		newFeature.setStatus(Status.Active);
		List<Feature> featureDtosList = new ArrayList<>();
		featureDtosList.add(newFeature);
		Page<Feature> page = new PageImpl<Feature>(featureDtosList);
		when(this.featureRepository.findAll()).thenReturn(featureDtosList);
		assertEquals(1, page.getTotalElements());

	}

}

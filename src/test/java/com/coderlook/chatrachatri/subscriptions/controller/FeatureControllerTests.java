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

import com.coderlook.chatrachatri.subscriptions.dto.CreateFeatureDto;
import com.coderlook.chatrachatri.subscriptions.dto.FeatureDto;
import com.coderlook.chatrachatri.subscriptions.dto.SuccessDto;
import com.coderlook.chatrachatri.subscriptions.entity.Feature;
import com.coderlook.chatrachatri.subscriptions.exceptions.APIException;
import com.coderlook.chatrachatri.subscriptions.exceptions.BusinessException;
import com.coderlook.chatrachatri.subscriptions.exceptions.ExceptionMessageConstants;
import com.coderlook.chatrachatri.subscriptions.mapper.FeatureMapper;
import com.coderlook.chatrachatri.subscriptions.service.FeatureService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class FeatureControllerTests {

	@InjectMocks
	public FeatureController featureController;

	@Mock
	private FeatureService featureService;

	@Mock
	private FeatureMapper featureMapper;

	@Autowired
	private MockMvc mockMvc;

	private static ObjectMapper mapper = new ObjectMapper();

	private String uriToFetchById = "/api/feature/{id}";
	private String uriToCreate = "/api/feature/create";
	private String uriToUpdate = "/api/feature/update";
	private String uriToDelete = "/api/feature/delete/{id}";
	private String uriToFetchList = "/api/feature/search";

	@BeforeEach
	public void SetupContext() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(featureController).build();

	}

	// Test Case for Feature Details

	@Test
	void testGetFeatureDetails_basic() throws Exception {

		FeatureDto featureDto = new FeatureDto();
		featureDto.setId(1L);
		featureDto.setName("Test Feature");

		when(this.featureService.getFeatureById(Mockito.anyLong())).thenReturn(
				new Feature());

		when(this.featureMapper.featureToFeatureDto(
				this.featureService.getFeatureById(Mockito.anyLong()))).thenReturn(
						featureDto);

		this.mockMvc.perform(get(uriToFetchById, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Feature"))
				.andReturn();

	}

	@Test
	void testGetFeature_internal_server_error() throws Exception {

		when(featureService.getFeatureById(Mockito.any())).thenThrow(new APIException(
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
	void testGetFeature_notFound() throws Exception {

		when(this.featureService.getFeatureById(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED));

		this.mockMvc.perform(get(uriToFetchById, 1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED));

	}

	// Test Case for Feature Create

	@Test
	void testFeatureCreate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Created Success");

		when(featureService.createFeature(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateFeatureDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Created Success"));

	}

	@Test
	void testFeatureCreate_internal_server_error() throws Exception {

		when(featureService.createFeature(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateFeatureDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testFeatureCreate_conflict() throws Exception {

		when(featureService.createFeature(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.CONFLICT),
						ExceptionMessageConstants.FAILED_TO_CREATE_FEATURE,
						ExceptionMessageConstants.FAILED_TO_CREATE_FEATURE));

		MockHttpServletRequestBuilder mockRequest = post(uriToCreate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new CreateFeatureDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isConflict())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_CREATE_FEATURE));

	}

	// Test Case for Update
	@Test
	void testFeatureUpdate_basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Updated Success");

		when(featureService.updateFeature(Mockito.any())).thenReturn(dto);

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new FeatureDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Updated Success"));

	}

	@Test
	void testFeatureUpdate_internal_server_error() throws Exception {

		when(featureService.updateFeature(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new FeatureDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testFeatureUpdate_badRequest() throws Exception {

		when(featureService.updateFeature(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_UPDATE_FEATURE,
						ExceptionMessageConstants.FAILED_TO_UPDATE_FEATURE));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new FeatureDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_UPDATE_FEATURE));

	}

	@Test
	void testFeatureUpdate_notFound() throws Exception {

		when(featureService.updateFeature(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new FeatureDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED));

	}

	// Test Case For Feature Delete

	@Test
	void testFeatureDelete_Basic() throws Exception {

		SuccessDto dto = new SuccessDto();
		dto.setMessage("Removed Success");

		when(featureService.deleteFeature(Mockito.any())).thenReturn(dto);

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
	void testFeatureDelete_internal_server_error() throws Exception {

		when(featureService.deleteFeature(Mockito.any())).thenThrow(new APIException(
				String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
				ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new FeatureDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

	@Test
	void testFeatureDelete_badRequest() throws Exception {

		when(featureService.deleteFeature(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST),
						ExceptionMessageConstants.FAILED_TO_DELETE_FEATURE,
						ExceptionMessageConstants.FAILED_TO_DELETE_FEATURE));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new FeatureDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.FAILED_TO_DELETE_FEATURE));

	}

	@Test
	void testFeatureDelete_notFound() throws Exception {

		when(featureService.deleteFeature(Mockito.any())).thenThrow(
				new BusinessException(String.valueOf(HttpStatus.NOT_FOUND),
						ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED,
						ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED));

		MockHttpServletRequestBuilder mockRequest = post(uriToDelete, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new FeatureDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.NOT_FOUND_FEATURE_ID_IS_REQUIRED));

	}

	// Test Case for Feature Search Details

	@Test
	void testGetSearchFeatures_basic() throws Exception {

		Feature newFeature = new Feature();
		newFeature.setId(1L);
		newFeature.setName("Test Feature");
		newFeature.setDisplayText("Test Display");

		Page<Feature> page = new PageImpl<>(Collections.singletonList(newFeature));

		when(this.featureService.search(Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(),
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
	void testGetSearchFeatures_internalservererror() throws Exception {

		when(featureService.search(Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any())).thenThrow(
						new BusinessException(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
								ExceptionMessageConstants.INTERNAL_SERVER_ERROR,
								ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

		MockHttpServletRequestBuilder mockRequest = get(uriToFetchList)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(new FeatureDto()));

		this.mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value(ExceptionMessageConstants.INTERNAL_SERVER_ERROR));

	}

}

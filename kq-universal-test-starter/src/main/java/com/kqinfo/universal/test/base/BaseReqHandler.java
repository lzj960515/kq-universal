package com.kqinfo.universal.test.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONUtil;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * 基础mock工具，用于发送rest请求
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
public class BaseReqHandler {

	private BaseReqHandler(){}

	public static JSONObject get(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, @Nullable MultiValueMap<String, String> params,
								 @Nullable Object... uriVars)  {
		MockHttpServletRequestBuilder builder;
		if (uriVars == null) {
			builder = MockMvcRequestBuilders.get(uri);
		}
		else {
			builder = MockMvcRequestBuilders.get(uri, uriVars);
		}
		if (params != null) {
			builder.queryParams(params);
		}
		if(headers != null){
			headers.forEach(builder::header);
		}
		return perform(mockMvc, builder);
	}

	public static JSONObject post(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body) {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(uri)
				.content(JSON.toJSONString(body).getBytes()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		if(headers != null){
			headers.forEach(builder::header);
		}
		return perform(mockMvc, builder);
	}
	public static JSONObject post(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body, Object... uriVars) {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(uri, uriVars)
				.content(JSON.toJSONString(body).getBytes()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		if(headers != null){
			headers.forEach(builder::header);
		}
		return perform(mockMvc, builder);
	}

	public static JSONObject put(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body) {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(uri)
				.content(JSON.toJSONString(body).getBytes()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		if(headers != null){
			headers.forEach(builder::header);
		}
		return perform(mockMvc, builder);
	}
	public static JSONObject put(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body, Object... uriVars) {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(uri, uriVars)
				.content(JSON.toJSONString(body).getBytes()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		if(headers != null){
			headers.forEach(builder::header);
		}
		return perform(mockMvc, builder);
	}

	public static JSONObject delete(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body) {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(uri);
		if(body != null){
			builder.content(JSON.toJSONString(body).getBytes()).accept(MediaType.APPLICATION_JSON);
		}
		builder.contentType(MediaType.APPLICATION_JSON);
		if(headers != null){
			headers.forEach(builder::header);
		}
		return perform(mockMvc, builder);
	}
	public static JSONObject delete(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body, Object... uriVars) {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(uri, uriVars);
		if(body != null){
			builder.content(JSON.toJSONString(body).getBytes()).accept(MediaType.APPLICATION_JSON);
		}
		builder.contentType(MediaType.APPLICATION_JSON);
		if(headers != null){
			headers.forEach(builder::header);
		}
		return perform(mockMvc, builder);
	}

	private static JSONObject perform(MockMvc mockMvc, MockHttpServletRequestBuilder builder) {
		try{
			ResultActions resultActions = mockMvc.perform(builder);

			MockHttpServletResponse response = resultActions.andReturn().getResponse();
			response.setCharacterEncoding("UTF-8");
			resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
			return JSON.parseObject(response.getContentAsString());
		}catch (Exception e){
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

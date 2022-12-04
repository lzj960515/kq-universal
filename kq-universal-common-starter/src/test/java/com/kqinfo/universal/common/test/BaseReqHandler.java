package com.kqinfo.universal.common.test;

import cn.hutool.json.JSONUtil;
import com.kqinfo.universal.common.response.BaseResult;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 基础mock工具，用于发送rest请求
 *
 * @author Zijian Liao
 * @see kq-universal-test-starter
 * @since 1.0.0
 */
@Deprecated
public final class BaseReqHandler {

    private BaseReqHandler() {
    }

    public static BaseResult<?> get(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, @Nullable MultiValueMap<String, String> params,
                                    @Nullable Object... uriVars) throws Exception {
        MockHttpServletRequestBuilder builder;
        if (uriVars == null) {
            builder = MockMvcRequestBuilders.get(uri);
        } else {
            builder = MockMvcRequestBuilders.get(uri, uriVars);
        }
        if (params != null) {
            builder.queryParams(params);
        }
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return perform(mockMvc, builder);
    }

    public static BaseResult<?> post(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(uri)
                .content(JSONUtil.toJsonStr(body).getBytes(StandardCharsets.UTF_8)).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return perform(mockMvc, builder);
    }

    public static BaseResult<?> post(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body, Object... uriVars) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(uri, uriVars)
                .content(JSONUtil.toJsonStr(body).getBytes(StandardCharsets.UTF_8)).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return perform(mockMvc, builder);
    }

    public static BaseResult<?> put(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(uri)
                .content(JSONUtil.toJsonStr(body).getBytes(StandardCharsets.UTF_8)).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return perform(mockMvc, builder);
    }

    public static BaseResult<?> put(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body, Object... uriVars) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(uri, uriVars)
                .content(JSONUtil.toJsonStr(body).getBytes(StandardCharsets.UTF_8)).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return perform(mockMvc, builder);
    }

    public static BaseResult<?> delete(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(uri)
                .content(JSONUtil.toJsonStr(body).getBytes(StandardCharsets.UTF_8)).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return perform(mockMvc, builder);
    }

    public static BaseResult<?> delete(MockMvc mockMvc, String uri, @Nullable Map<String, String> headers, Object body, Object... uriVars) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(uri, uriVars)
                .content(JSONUtil.toJsonStr(body).getBytes(StandardCharsets.UTF_8)).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return perform(mockMvc, builder);
    }

    private static BaseResult<?> perform(MockMvc mockMvc, RequestBuilder builder) throws Exception {
        ResultActions resultActions = mockMvc.perform(builder);

        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        response.setCharacterEncoding("UTF-8");
        resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
        return JSONUtil.toBean(response.getContentAsString(), BaseResult.class);
    }

}

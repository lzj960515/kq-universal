package com.kqinfo.universal.test.base;

import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

/**
 * Restful请求基础测试类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public abstract class BaseRestTest extends BaseTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @BeforeEach
    public void init5() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    protected JSONObject get(String uri, @Nullable Map<String, String> headers, @Nullable MultiValueMap<String, String> params,
                             @Nullable Object... uriVars) {
        return BaseReqHandler.get(mockMvc, uri, headers, params, uriVars);
    }

    protected JSONObject post(String uri, @Nullable Map<String, String> headers, Object body)  {
        return BaseReqHandler.post(mockMvc, uri, headers, body);
    }

    protected JSONObject post(String uri, @Nullable Map<String, String> headers, Object body, @Nullable Object... uriVars)  {
        return BaseReqHandler.post(mockMvc, uri, headers, body, uriVars);
    }

    protected JSONObject put(String uri, @Nullable Map<String, String> headers, Object body)  {
        return BaseReqHandler.put(mockMvc, uri, headers, body);
    }

    protected JSONObject put(String uri, @Nullable Map<String, String> headers, Object body, @Nullable Object... uriVars)  {
        return BaseReqHandler.put(mockMvc, uri, headers, body, uriVars);
    }

    protected JSONObject delete(String uri, @Nullable Map<String, String> headers, Object body)  {
        return BaseReqHandler.delete(mockMvc, uri, headers, body);
    }

    protected JSONObject delete(String uri, @Nullable Map<String, String> headers, Object body, @Nullable Object... uriVars)  {
        return BaseReqHandler.delete(mockMvc, uri, headers, body, uriVars);
    }
}

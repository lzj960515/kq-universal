package com.kqinfo.universal.common.test;

import com.kqinfo.universal.common.response.BaseResult;
import org.junit.Before;
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
 * @see kq-universal-test-starter
 * @since 1.0.0
 */
@Deprecated
public abstract class BaseRestTest extends BaseTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    protected BaseResult<?> get(String uri, @Nullable Map<String, String> headers, @Nullable MultiValueMap<String, String> params,
                                @Nullable Object... uriVars) throws Exception {
        return BaseReqHandler.get(mockMvc, uri, headers, params, uriVars);
    }

    protected BaseResult<?> post(String uri, @Nullable Map<String, String> headers, Object body) throws Exception {
        return BaseReqHandler.post(mockMvc, uri, headers, body);
    }

    protected BaseResult<?> post(String uri, @Nullable Map<String, String> headers, Object body, @Nullable Object... uriVars) throws Exception {
        return BaseReqHandler.post(mockMvc, uri, headers, body, uriVars);
    }

    protected BaseResult<?> put(String uri, @Nullable Map<String, String> headers, Object body) throws Exception {
        return BaseReqHandler.put(mockMvc, uri, headers, body);
    }

    protected BaseResult<?> put(String uri, @Nullable Map<String, String> headers, Object body, @Nullable Object... uriVars) throws Exception {
        return BaseReqHandler.put(mockMvc, uri, headers, body, uriVars);
    }

    protected BaseResult<?> delete(String uri, @Nullable Map<String, String> headers, Object body) throws Exception {
        return BaseReqHandler.delete(mockMvc, uri, headers, body);
    }

    protected BaseResult<?> delete(String uri, @Nullable Map<String, String> headers, Object body, @Nullable Object... uriVars) throws Exception {
        return BaseReqHandler.delete(mockMvc, uri, headers, body, uriVars);
    }
}

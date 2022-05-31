package com.kqinfo.universal.common.test;

import cn.hutool.json.JSONObject;
import com.kqinfo.universal.common.response.BaseResult;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class DateTimeTest extends BaseRestTest {

    @Test
    public void testPost() throws Exception {
        JSONObject user = new JSONObject();
        user.set("birthday", "2020-10-20");
        user.set("createTime", "2019-06-19T07:19:39Z");
        BaseResult<?> baseResult = super.post("/user", null, user);
        MatcherAssert.assertThat(baseResult.isSuccess(), CoreMatchers.is(true));
    }

    @Test
    public void testGet() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("localDateTime", "2019-06-19T07:19:39Z");
        params.add("localDate", "2019-06-19");
        BaseResult<?> baseResult = super.get("/user", null, params);
        MatcherAssert.assertThat(baseResult.isSuccess(), CoreMatchers.is(true));
    }
}

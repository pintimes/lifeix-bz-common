package com.lifeix.test.bz.common;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import com.lifeix.bz.common.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev,common,system")
public class BaseTest {

	@Autowired
	public WebApplicationContext context;

	public MockMvc mvc;

	@Before
	public void setUp() throws Exception {
		this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

	public static String getResult(MockMvc mvc, MockHttpServletRequestBuilder builder, HttpHeaders httpHeaders, String content) throws Exception {
		builder.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		if (httpHeaders != null) {
			builder.headers(httpHeaders);
		}
		if (!StringUtils.isEmpty(content)) {
			builder.content(content);
		}
		return getResult(mvc, builder);
	}

	public static String getResult(MockMvc mvc, MockHttpServletRequestBuilder builder) throws Exception {
		ResultActions resultActions = mvc.perform(builder);
		resultActions.andDo(MockMvcResultHandlers.print());
		MvcResult result = resultActions.andReturn();
		resultActions.andExpect(status().isOk());
		MockHttpServletResponse response = result.getResponse();
		String content = response.getContentAsString();
		return content;
	}

}

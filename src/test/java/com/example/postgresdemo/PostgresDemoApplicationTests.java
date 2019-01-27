package com.example.postgresdemo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostgresDemoApplicationTests {


	// Need to split out stuff as
	// Controllers with injected (and thereby mockable) services
	// Service with injected (and thereby mockable) repositories
	// and can then get some decent testing started

	// Probably ought to also have Service AND ServiceImpl to keep things decoupled






	// Make sure we can start
	@Test
	public void contextLoads() {
	}

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setUp() {
		this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

	@Test
	public void testQuestions() throws Exception {

		this.mvc.perform(get("/questions")).andExpect(status().isOk());
//				.andExpect(content().string("Bath"));
	}

	@Test
	public void testQuestionAndAnswers() throws Exception {

		this.mvc.perform(get("/questions/99/answers")).andExpect(status().isOk());
//				.andExpect(content().string("Bath"));
	}

	@Test
	public void testQuestionAndAnswersMissing() throws Exception {

		this.mvc.perform(get("/questions/0/answers")).andExpect(status().isOk());
//				.andExpect(content().string("Bath"));
	}


//	@Test
//	public void testJmx() throws Exception {
//		assertThat(ManagementFactory.getPlatformMBeanServer()
//				.queryMBeans(new ObjectName("jpa.sample:type=ConnectionPool,*"), null))
//				.hasSize(1);
//	}













}

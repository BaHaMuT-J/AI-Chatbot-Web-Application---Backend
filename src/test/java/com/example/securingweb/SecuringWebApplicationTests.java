//package com.example.securingweb;
//
//import io.muzoo.ssc.project.backend.SecuringWebApplication;
//import org.junit.jupiter.api.Test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
//import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
//import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(classes = SecuringWebApplication.class)
//@AutoConfigureMockMvc
//public class SecuringWebApplicationTests {
//	@Autowired
//	private MockMvc mockMvc;
//
//	@Test
//	public void loginWithValidUserThenAuthenticated() throws Exception {
//		FormLoginRequestBuilder login = formLogin()
//			.user("user")
//			.password("password");
//
//		mockMvc.perform(login)
//			.andExpect(authenticated().withUsername("user"));
//	}
//
//	@Test
//	public void loginWithInvalidUserThenUnauthenticated() throws Exception {
//		FormLoginRequestBuilder login = formLogin()
//			.user("invalid")
//			.password("invalidpassword");
//
//		mockMvc.perform(login)
//			.andExpect(unauthenticated());
//	}
//
//	@Test
//	public void accessUnsecuredResourceThenOk() throws Exception {
//		mockMvc.perform(get("/"))
//			.andExpect(status().isOk());
//	}
//
//	@Test
//	public void accessSecuredResourceUnauthenticatedThenRedirectsToLogin() throws Exception {
//		mockMvc.perform(get("/hello"))
//			.andExpect(status().is3xxRedirection())
//			.andExpect(redirectedUrlPattern("**/login"));
//	}
//
//	@Test
//	@WithMockUser
//	public void accessSecuredResourceAuthenticatedThenOk() throws Exception {
//		MvcResult mvcResult = mockMvc.perform(get("/api/logout"))
//				.andExpect(status().isOk())
//				.andReturn();
//
//		assertThat(mvcResult.getResponse().getContentAsString()).contains("You are successfully logged out.");
//	}
//}

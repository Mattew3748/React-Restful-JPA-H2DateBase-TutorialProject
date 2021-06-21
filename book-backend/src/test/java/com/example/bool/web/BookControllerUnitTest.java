package com.example.bool.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.example.bool.domain.Book;
import com.example.bool.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

//단위 테스트 : (Controller 관련 로직만 띄우기) Filter,ControllerAdvice

@Slf4j
@WebMvcTest
public class BookControllerUnitTest {

	@Autowired
	private MockMvc mockMvc; // 주소호출을 해서 테스트를 해주는 도구
	
	@MockBean
	private BookService bookservice;
	

	@Test
	public void save_테스트() throws Exception {
		// given (테스트를 하기 위한 준비)
		// 데이터파싱해서 json타입으로 변환해준다. 반대로바꾸는거는 ObjectMapper().readValue
		Book book = new Book(null, "스프링 따라하기", "코스");
		String content = new ObjectMapper().writeValueAsString(book);
		when(bookservice.저장하기(book)).thenReturn(new Book(1L, "스프링 따라하기", "코스"));
		// 스텁 : service랑 Repository를 타지 않기 때문에미리 결과가 이렇게 나올것이라고 정의해놓는것,

		// when(테스트 실행)
		ResultActions resultAction = mockMvc.perform(post("/book")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));

		// then (검증)
		// $ : 전체결과
		// 상태는 만들어진상태를 기대하고 반환값중 title = 스프링 따라하기가 나올것을 기대한다.
		resultAction
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.title").value("스프링 따라하기"))
		.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void find_테스트() throws Exception  {
		//given
		
		List<Book> book = new ArrayList<>();
		book.add(new Book(1L,"스프링부트 따라하기","코스"));
		book.add(new Book(2L,"리엑트 따라하기","모스"));
		when(bookservice.모두가져오기()).thenReturn(book);
		// Repository가 연결안되있으므로 가상의 Respository를 만듬
		
		//when
		ResultActions resultAction = mockMvc.perform(get("/book")
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then
		resultAction
		.andExpect(status().isOk())
		.andExpect(jsonPath("$",Matchers.hasSize(2)))
		.andExpect(jsonPath("$.[0].title").value("스프링부트 따라하기"))
		.andDo(MockMvcResultHandlers.print());
	}
	@Test
	public void findById_테스트() throws Exception{
		//given
		Long id = 1L;
		when(bookservice.한건가져오기(id)).thenReturn(new Book(1L,"자바 공부하기","쌀"));
		
		//when
		ResultActions resultAction = mockMvc.perform(get("/book/{id}",id)
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then
		resultAction
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title").value("자바 공부하기"))
		.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void update_테스트() throws Exception{
		//given
		Long id = 1L;
		Book book = new Book(null,"C++ 따라하기","코스");
		String content = new ObjectMapper().writeValueAsString(book);
		
		when(bookservice.수정하기(id,book)).thenReturn(new Book(1L,"C++ 따라하기","코스"));
		
		//when
		ResultActions resultAction = mockMvc.perform(put("/book/{id}",id)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then
		resultAction
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title").value("C++ 따라하기"))
		.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void delete_테스트() throws Exception{
		//given
		Long id = 1L;
		when(bookservice.삭제하기(id)).thenReturn("ok");
		
		//when
		ResultActions resultAction = mockMvc.perform(delete("/book/{id}",id)
				.accept(MediaType.TEXT_PLAIN));
		
		//then
		resultAction
		.andExpect(status().isOk())
		.andDo(MockMvcResultHandlers.print());
		
		MvcResult requestResult = resultAction.andReturn();
		String result = requestResult.getResponse().getContentAsString();
		
		assertEquals("ok", result);
	}
}

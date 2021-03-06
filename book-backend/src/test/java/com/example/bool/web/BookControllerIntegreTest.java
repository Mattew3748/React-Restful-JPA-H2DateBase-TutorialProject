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

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.aspectj.lang.annotation.Before;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.example.bool.domain.Book;
import com.example.bool.domain.BookRepository;
import com.example.bool.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

// 통합 테스트 : (모든 Bean들을 똑같이 Ioc 올리고 테스트 하는것)
// - 모든 Bean다 올려서 Test한다.
// 장점 : 통합 테스트가 정상작동되면 실제 동작에서도 정상작동된다.
// 단점 : 느리다
// @SpringBootTest에는 @ExtendWith(SpringExtension.class) 스프링으로 Test할수 있게 확장시켜주는 어노테이션이 포함되어 있다
// WebEnvironment.MOCK : 실제 톰켓을 올리는게 아니라, 다른 톰켓으로 테스트
// WebEnvironment.RANDOM_PORT : 실제 톰캣으로 테스트
// @AutoConfigureMockMvc MockMvc를 Ioc에 등록해줌.
// @Transactional은 각S 각의 테스트함수가 종료될 때마다 트렌잭션을 rollback 해주는 어노테이션
//                   - 그다음 Test가 이전 Test에 영향을 미치지 않음(독립적)/ 해당어노테이션을 사용하지않으면 모든 테스트가 끝난 후 rollback이 된다.


@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK) // 
public class BookControllerIntegreTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@BeforeEach // 모든 Test함수가 실행되기 직전에 각 각 실행된다.
	public void init() {
		entityManager.createNativeQuery("ALTER TABLE book ALTER COLUMN id RESTART WITH 1").executeUpdate();
	}// ALTER TABLE book ALTER COLUMN id RESTART WITH 1 >> H2 database 문법
	
	//BDDMockito 패턴 given, when, then
	@Test
	public void save_테스트() throws Exception {
		//given (테스트를 하기 위한 준비)
		// 데이터파싱해서 json타입으로 변환해준다. 반대로바꾸는거는 ObjectMapper().readValue
		Book book = new Book(null,"스프링 따라하기","코스");
		String content = new ObjectMapper().writeValueAsString(book);
		
		// when(테스트 실행)
		ResultActions resultAction= mockMvc.perform(post("/book")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		// then (검증)
		// $ : 전체결과 
		// 상태는 만들어진상태를 기대하고 반환값중 title = 스프링 따라하기가 나올것을 기대한다.
		resultAction.andExpect(status().isCreated())
		.andExpect(jsonPath("$.title").value( "스프링 따라하기"));
	}

	@Test
	public void find_테스트() throws Exception  {
		//given
		List<Book> book = new ArrayList<>();
		book.add(new Book(1L,"스프링부트 따라하기","코스"));
		book.add(new Book(2L,"리엑트 따라하기","모스"));
		book.add(new Book(3L,"Junit5 따라하기","산타"));
		bookRepository.saveAll(book);
		
		// Repository가 연결안되있으므로 가상의 Respository를 만듬
		
		//when
		ResultActions resultAction = mockMvc.perform(get("/book")
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then
		resultAction
		.andExpect(status().isOk())
		.andExpect(jsonPath("$",Matchers.hasSize(3)))
		.andExpect(jsonPath("$.[0].title").value("스프링부트 따라하기"))
		.andDo(MockMvcResultHandlers.print());
	}
	@Test
	public void findById_테스트() throws Exception{
		//given
		Long id = 2L;
		List<Book> book = new ArrayList<>();
		book.add(new Book(1L,"스프링부트 따라하기","코스"));
		book.add(new Book(2L,"리엑트 따라하기","모스"));
		book.add(new Book(3L,"Junit5 따라하기","산타"));
		bookRepository.saveAll(book);
		
		//when
		ResultActions resultAction = mockMvc.perform(get("/book/{id}",id)
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then
		resultAction
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title").value("리엑트 따라하기"))
		.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void update_테스트() throws Exception{
		//given
		Long id = 1L;
		List<Book> books = new ArrayList<>();
		books.add(new Book(null,"스프링부트 따라하기","코스"));
		books.add(new Book(null,"리엑트 따라하기","모스"));
		books.add(new Book(null,"Junit5 따라하기","산타"));
		bookRepository.saveAll(books);
		
		Book book = new Book(null,"C++ 따라하기","모니카");
		String content = new ObjectMapper().writeValueAsString(book);
		
		
		//when
		ResultActions resultAction = mockMvc.perform(put("/book/{id}",id)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));

		//then
		resultAction
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(1L))
		.andExpect(jsonPath("$.title").value("C++ 따라하기"))
		.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void delete_테스트() throws Exception{
		//given
		Long id = 1L;
		List<Book> books = new ArrayList<>();
		books.add(new Book(null,"스프링부트 따라하기","코스"));
		books.add(new Book(null,"리엑트 따라하기","모스"));
		books.add(new Book(null,"Junit5 따라하기","산타"));
		bookRepository.saveAll(books);
		
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

package com.example.bool.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.bool.domain.Book;
import com.example.bool.domain.BookRepository;

// 단위 테스트 (Service와 관련된 애들만 메모리에 띄우면 됨.)
// - 최소한의 Bean으로만 Test
// 장점 : 빠르다.
// 단점 : 단위테스트가 정상 작동하더라도 실제 동작에서 다른 결과가 나올 수 있음

// @ExtendWith(SpringExtension.class) 스프링으로 Test할수 있게 확장시켜주는 어노테이션
// Serivce를 테스트하기 위해서 BoardRepository가 Service DI되어있어야한다. 그렇다고 BoardRepository도 함께 Test Bean에 등록해준다면
// 단위테스트의 의미가 없어진다. 해결방법은 BoardRepository를 가짜 객체로 만들수 있다.

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

	@InjectMocks // BookSerivce객체가 만들어질 때 BookServiceUnitTest 파일에 @Mock로 등록된 모든 애들을 주입받는다.
	private BookService bookService;

	@Mock
	private BookRepository bookRepository;
	
	@Test
	public void save_테스트() {
		//given
		Book book = new Book(null,"책제목","책저자1");
		
		//when
		when(bookRepository.save(book)).thenReturn(book);
		
		//test execute
		Book bookEntity = bookService.저장하기(book);
		
		//then
		assertEquals("책제목", bookEntity.getTitle());
	}
}

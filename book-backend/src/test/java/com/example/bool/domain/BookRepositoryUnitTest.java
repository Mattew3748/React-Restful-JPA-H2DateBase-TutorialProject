package com.example.bool.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

// 단위 테스트 (DB 관련된 Bean이 Ioc에 등록되면 됨)

@Transactional
@AutoConfigureTestDatabase(replace = Replace.ANY) // 가짜 디비로 테스트, Replace.NONE 실제 DB로 테스트(통합테스트에서 사용)
@DataJpaTest // JPA 관련된 애들만 뜸
public class BookRepositoryUnitTest {

	@Autowired
	private BookRepository bookRepository;
	
	@Test
	public void save_테스트() {
		//given
		Book book = new Book(null,"책제목","책저자1");
		
		//when
		Book bookEntity = bookRepository.save(book);
		
		//then
		assertEquals("책제목", bookEntity.getTitle());
	}
}

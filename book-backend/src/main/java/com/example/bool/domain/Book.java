package com.example.bool.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity // 서버 실행시에 테이블이 h2에 생성됨.
public class Book {
	@Id //PK값으로설정
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment하겠다
	private Long id;
	
	private String title;
	private String author;

}

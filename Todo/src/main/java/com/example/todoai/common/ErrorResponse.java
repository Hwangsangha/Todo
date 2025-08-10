package com.example.todoai.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

//클라이언트로 내려줄 표준 에러 응답 형태
@Getter
@AllArgsConstructor
public class ErrorResponse {
	private final String time;		//에러 발생 시각
	private final int status;		//http상태코드
	private final String error;		//상태코드 텍스트
	private final String message;	//개발자가 남긴 구체 메시지
	private final String path;		//요청 url
}

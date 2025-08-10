package com.example.todoai.common;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

//컨트롤러 전역에서 발생하는 예외를 한 곳에서 JSON으로 처리
//각 예외 유형별로 적절한 Http 상태 코드와 메시지를 내려준다.
@RestControllerAdvice	//모든 RestController에 대한 전역 예외 처리 활성화
public class GlobalExceptionHandler {
	//공통 응답 빌드 유틸(중복 제거)
	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest req){
		ErrorResponse body = new ErrorResponse(
				OffsetDateTime.now().toString(),	//ISO-8601시각
				status.value(),						//400, 404, 500 등 상태코드
				status.getReasonPhrase(),			//Bad Request, Not Found 등 메시지
				message,							//구체 메시지
				req.getRequestURI()					//요청 경로
		);
		return ResponseEntity.status(status).body(body);
	}
	
	public ResponseEntity<ErrorResponse> handleBadBody(HttpMessageNotReadableException e, HttpServletRequest req){
		return build(HttpStatus.BAD_REQUEST, null, req);
	}
}

package com.example.todoai.common;

import java.time.OffsetDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
	
	/*
	 * 잘못된 JSON, enum 파싱 실패
	 * 예 - priority에 "TOP" 같은 허용되지 않는 문자열을 보낸 경우
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleBadBody(HttpMessageNotReadableException e, HttpServletRequest req){
		String msg = "요청 본문(JSON) 형식 오류: " + (e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage());
		return build(HttpStatus.BAD_REQUEST, msg, req);
	}
	
	//RequestParam타입 불일치 userId=문자열
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest req){
		return build(HttpStatus.BAD_REQUEST, "파라미터 타입이 올바르지 않습니다: " + e.getName(), req);
	}
	
	//필수 파라미터 누락 userId빠짐
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException e, HttpServletRequest req){
		return build(HttpStatus.BAD_REQUEST, "필수 파라미터가 없습니다: " + e.getParameterName(), req);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req){
		String msg = e.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(fe -> fe.getField() + " " + fe.getDefaultMessage())
				.orElse("유효성 검증에 실패했습니다.");
		return build(HttpStatus.BAD_REQUEST, msg, req);
	}
	
	//무결성 제약 위반 (NOT NULL, UNIQUE 등)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handlNotFound(ResourceNotFoundException e, HttpServletRequest req){
		return build(HttpStatus.NOT_FOUND, e.getMessage(), req);
	}
	
	//그 외 예기치 못한 예외(맨 마지막 안전망)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleEtc(Exception e, HttpServletRequest req){
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류: " + e.getMessage(), req);
	}
}

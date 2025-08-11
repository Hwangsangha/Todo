package com.example.todoai.common;

/*리소스(사용자, 할 일 등)를 찾지 못했을 때 사용하기 위한 런탕미 예외.
Service/Contorller에서 던지면 GlobalExceptionHandler가 404로 변환해서 내려줌*/
public class ResourceNotFoundException extends RuntimeException{
	public ResourceNotFoundException(String message) {
		super(message);	//부모(RuntimeException)의 생성자에 메시지 전달
	}
}

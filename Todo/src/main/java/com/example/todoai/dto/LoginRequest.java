package com.example.todoai.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO (API 입력 전용)
 * - username / password 둘 다 공백 허용 안 함
 */
public record LoginRequest(
		@NotBlank(message = "username은 필수") String username,
		@NotBlank(message = "passoword는 필수") String password
) {}

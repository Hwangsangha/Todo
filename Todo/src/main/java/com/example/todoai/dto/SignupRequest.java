package com.example.todoai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
		@NotBlank(message = "username은 필수") String username,
		@Email(message = "이메일 형식 아님") @NotBlank(message = "email은 필수") String email,
		@NotBlank(message = "password는 필수") String password
) {}
package com.example.todoai.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeCotroller {
	@GetMapping("/me")
	public Map<String, Object> me(
		@org.springframework.security.core.annotation.AuthenticationPrincipal(expression = "attributes")
		Map<String, Object> attrs) {
		return attrs;
	}
	@GetMapping("/")
	public String home() {
		return "ok";
	}
}

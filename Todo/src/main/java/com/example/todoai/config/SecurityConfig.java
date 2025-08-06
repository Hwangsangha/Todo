package com.example.todoai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
	  @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	        .csrf(csrf -> csrf.disable()) //POST, PUT, DELETE 요청 등에 대한 보안
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) //보안을 위해 iframe삽입을 막음, h2콘솔은 iframe기바능로 렌더링 되기 때문에, 이 설정을 비활성화해야 콘솔이 보임.
            .authorizeHttpRequests(auth -> auth //요청별 인가 규칙 정의 이후 requestMatchers나 anyRequest등으로 상세 인가 조건 설정
                .requestMatchers("/h2-console/**").permitAll() //h2콘솔 경로는 인증 없이 접근 허용
                .anyRequest().permitAll()//모든 요청 인증없이 허용 실무에서는 보통 authenticated로 설정하고 로그인 필터를 적용
            )
            .formLogin(form -> form.disable());//기본 로그인 폼 화면 비활성화

	        return http.build();
	    }
}

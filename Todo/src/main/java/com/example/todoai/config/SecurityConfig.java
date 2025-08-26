package com.example.todoai.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	/*
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		.csrf(csrf -> csrf.disable()) //POST, PUT, DELETE 요청 등에 대한 보안
		.headers(headers -> headers.frameOptions(frame -> frame.disable())) //보안을 위해 iframe삽입을 막음, h2콘솔은 iframe기바능로 렌더링 되기 때문에, 이 설정을 비활성화해야 콘솔이 보임.
        .authorizeHttpRequests(auth -> auth //요청별 인가 규칙 정의 이후 requestMatchers나 anyRequest등으로 상세 인가 조건 설정
        .requestMatchers("/h2-console/**").permitAll() //h2콘솔 경로는 인증 없이 접근 허용
        .anyRequest().permitAll()	//모든 요청 인증없이 허용 실무에서는 보통 authenticated로 설정하고 로그인 필터를 적용)
        .formLogin(form -> form.disable());	//기본 로그인 폼 화면 비활성화

	    return http.build();
	}*/
//	//목적: (개발용) 인메모리 사용자 1명 등록 > Postman에서 Basic Auth로 사용
//	@Bean
//	public UserDetailsService users() {
//		UserDetails user = User.withUsername("testuser")
//				.password("{noop}1234")
//				.roles("USER")
//				.build();
//		return new InMemoryUserDetailsManager(user);
//	}
	
	//목적: 모든 /api/** 요청은 인증 필요. (개발용) HTTP Basic 사용
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http.authorizeHttpRequests(a -> a
					.requestMatchers("/", "/error", "/health", "/oauth2/**", "/login/**", "/login/oauth2/**").permitAll()
					.anyRequest().authenticated()
				)
				.oauth2Login(o -> o
						.authorizationEndpoint(a -> a.baseUri("/oauth2/authorization"))
						.loginPage("/oauth2/authorization/google"))
				.logout(logout -> logout.logoutSuccessUrl("/"));
		
		http.csrf(c -> c.ignoringRequestMatchers("/oauth2/**", "/login/**", "/login/oauth2/**"));
		
		return http.build();
	}
	@Bean
	CommandLineRunner checkRegs(ClientRegistrationRepository repo) {
	    return args -> {
	        ((InMemoryClientRegistrationRepository) repo)
	            .forEach(r -> System.out.println("OAUTH2 REG: " + r.getRegistrationId()));
	    };
	}

}

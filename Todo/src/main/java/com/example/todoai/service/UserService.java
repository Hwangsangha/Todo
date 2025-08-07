package com.example.todoai.service;

import org.springframework.stereotype.Service;

import com.example.todoai.entity.User;
import com.example.todoai.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	//UserRepository를 의존성 주입 받아 DB작업을 처리
	private final UserRepository userRepository;
	
	//사용자 생성
	//user 저장할 사용자 객체
	//return 저장된 사용자 정보
	public User creatUser(User user) {
		return userRepository.save(user);
	}
	
	//id조회할 사용자 ID
	//해당 ID의 사용자 정보 - 없으면 null반환
	public User getUser(Long id) {
		return userRepository.findById(id).orElse(null);
	}
	
	public User updatedUser(Long id, User updatedUser) {
		User user = userRepository.findById(id).orElse(null);
		if(user != null) {
			user.setUsername(updatedUser.getUsername());
			user.setEmail(updatedUser.getEmail());
			return userRepository.save(user);
		}
		return null;	//ID에 해당하는 사용자 없을 경우 null반환
	}
	
	//사용자 ID삭제
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
}

package com.example.todoai.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.todoai.entity.User;

//User엔티티에 대한 DB접근을 처리하는 JpaRepository
public interface UserRepository extends JpaRepository<User, Long>{
	
	//username으로 사용자 찾기(로그인, 중복 체크 등에 사용 예정)
	Optional<User> findByUsername(String username);
}

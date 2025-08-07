package com.example.todoai.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.todoai.entity.Task;

//Task엔티티에 대한 DB접근을 처리하는 JpaRepository
//JpaRepository를 상속하면 CRUD메서드 자동생성
public interface TaskRepository extends JpaRepository<Task, Long>{
	
	//특정 사용자의 할일 목록 전체 조회
	List<Task>	findByUserId(Long userId);
}

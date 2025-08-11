package com.example.todoai.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.todoai.entity.Task;
import com.example.todoai.entity.User;
import com.example.todoai.service.TaskService;
import com.example.todoai.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;	//할 일 로직 처리 서비스
	private final UserService userService;	//사용자 로직 처리 서비스
	
	@PostMapping	//HTTP POST 요청 처리
	public ResponseEntity<Task> create(@RequestParam("userId") Long userId, @RequestBody Task task){
		User owner = userService.getUser(userId);	//사용자 조회
		task.setUser(owner);						//연관관계 주입
		Task created = taskService.createdTask(task);	//저장
		return ResponseEntity
				.created(URI.create("/api/tasks/" + created.getId()))	//HTTP201 + Location 헤더
				.body(created);	//저장된 Task JSON 반환
	}
	
	@GetMapping("/{id}")	// /api/tasks/1
	public ResponseEntity<Task> get(@PathVariable("id") Long id){
		return ResponseEntity.ok(taskService.getTask(id));	//HTTP 200 + Task JSON
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Task> update(@PathVariable("id") Long id, @RequestBody Task task){
		return ResponseEntity.ok(taskService.updateTask(id, task));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id){
		taskService.deleteTask(id);
		return ResponseEntity.noContent().build();	//HTTP 204(본문없음)
	}
	
	//특정 사용자 Task 목록 조회
	@GetMapping
	public ResponseEntity<List<Task>> listByUser(@RequestParam("userId") Long userId){
		return ResponseEntity.ok(taskService.getTasksByUser(userId));
	}
}

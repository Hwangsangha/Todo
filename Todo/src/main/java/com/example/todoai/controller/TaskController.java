package com.example.todoai.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.todoai.common.ResourceNotFoundException;
import com.example.todoai.dto.TaskCreateRequest;
import com.example.todoai.dto.TaskResponse;
import com.example.todoai.dto.TaskUpdateRequest;
import com.example.todoai.entity.Task;
import com.example.todoai.service.TaskService;
import com.example.todoai.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;	//할 일 로직 처리 서비스
	private final UserService userService;	//사용자 로직 처리 서비스
	
	//할일 생성
	@PostMapping	//HTTP POST 요청 처리
	public ResponseEntity<TaskResponse> create(
			@RequestParam("userId") Long userId,
			@Valid @RequestBody TaskCreateRequest req){
		
		//서비스에 위임) userId 검증 + DTO > 엔티티 매핑 + 저장
		Task created = taskService.createdTask(userId, req);	//저장
		
		//rest규약: Location헤더에 생성 지원의 URI 제공
		//보안/캡슐화: 엔티티 대신 응답 DTO로 변환하여 반환
		return ResponseEntity
				.created(URI.create("/api/tasks/" + created.getId()))	//HTTP201 + Location 헤더
				.body(TaskResponse.from(created));	//저장된 Task JSON 반환
	}
	
	//특정 할일 조회
	@GetMapping("/{id}")	// /api/tasks/1
	public ResponseEntity<TaskResponse> get(@PathVariable("id") Long id){
		
		//서비스에 위임: 존재하지 않으면 ResourceNotFoundException 발생(전역 예외 처리 대상)
		Task task = taskService.getTask(id);
		
		//엔티티 > 응답 DTO 변환 후 반환
		return ResponseEntity.ok(TaskResponse.from(task));	//HTTP 200 + Task JSON
	}
	
	//할일 수정
	//Task엔티티를 직접 받지 않고, 요청 DTO로 부분수정.
	//부분수정 + 엔티티 노출 방지 + 일관된 응답 규약
	@PatchMapping("/{id}")
	public ResponseEntity<Void> update(
			@PathVariable("id") Long id,
			@Valid @RequestBody TaskUpdateRequest req){
		
		//서비스에 위임: 존개 검증 후 , null이 아닌 필드만 반영하여 저장
		taskService.updateTask(id, req);
		
		//본문 없는 성고 응답(수정 완료, 반환할 리소스 없음)
		return ResponseEntity.noContent().build();
	}
		
	
	//할일 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id){
		taskService.deleteTask(id);
		return ResponseEntity.noContent().build();	//HTTP 204(본문없음)
	}
	
	//전체 Task 목록 조회
	//목록 조회에서 Task엔티티 직접 노출을 막음
	//응답 전용 DTO리스트로 반환
	@GetMapping
	public ResponseEntity<List<TaskResponse>> listByUser(@RequestParam("userId") Long userId){
		if(userService.getUser(userId) == null) throw new ResourceNotFoundException("사용자를 찾을 수 없습니다. id=" + userId);
		
		//서비스에서 해당 사용자 소유 Task 목록 조회
		List<Task> tasks = taskService.getTasksByUser(userId);
		
		//엔티티 > 응답 DTO로 변환(스트림 사용)
		List<TaskResponse> result = tasks.stream()	//List<Task>(엔티티 목록)dmf stream호출로 Stream<Task>객체 생성
				.map(TaskResponse::from)	//map으로 스트림의 각 원소를 다른값으로 변환 - TaskResponse.from(task)로 변환 > Stream<TaskResponse>
				.collect(Collectors.toList());	//Stream<TaskResponse>를 List<TaskResponse> 형태로 반환
		return ResponseEntity.ok(result);
	}
}

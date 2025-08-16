package com.example.todoai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.todoai.common.ResourceNotFoundException;
import com.example.todoai.dto.TaskCreateRequest;
import com.example.todoai.dto.TaskUpdateRequest;
import com.example.todoai.entity.Task;
import com.example.todoai.entity.User;
import com.example.todoai.repository.TaskRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {
		private final TaskRepository taskRepository;
		private final UserService userService;
		
		public Task createdTask(Task task) {
		    return taskRepository.save(task);
		}
		
		//task저장할 할 일 정보
		public Task createdTask(Long userId, TaskCreateRequest req) {
			User owner = userService.getUser(userId);
			if(owner == null) {
				throw new ResourceNotFoundException("사용자를 찾을 수 없습니다. id=" + userId);
			}
			
			Task t = new Task();
			t.setUser(owner);
			t.setTitle(req.getTitle());
			t.setDescription(req.getDescription());
			t.setPriority(req.getPriority());
			t.setStatus(req.getStatus());
			//Task의 dueDate타입이 String이면 그대로, LocalDate면 아래처럼 파싱
			//t.setDueDate(LocalDate.parse(req.getDueDate()));
			
			return taskRepository.save(t);
		}
		
		//taskid 객체 조회 - 없으면 null
		public Task getTask(Long id) {
			Task task = taskRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("할 일을 잦을 수 없습니다. id=" + id));
			
			if(task.getUser() != null) {
				task.getUser().getUsername();	// DB에서 실제 User 데이터 로딩
			}
			return task;
		}
		
		//task 수정
		//목적: 부분수정을 위해 DTO의 "null이 아닌 필드만" 엔티티에 반영한다.
		//흐름: 존재검증 > 변경필듬나 set > save
		//주의: Task엔티티에 없는 필드는 매핑하지 않음
		public Task updateTask(Long id, TaskUpdateRequest req) {
			//존재검증: 없으면 404 성격의 예외 던짐(전역 예외 핸들러 대상)
			Task t = taskRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Task를 찾을수 없습니다. id=" + id));
			
			//부분 수정: DTO에서 null이 아닌 값만 반영
			if(req.getTitle() != null) {
				t.setTitle(req.getTitle());
			}
			if(req.getDescription() != null) {
				t.setDescription(req.getDescription());
			}
			if(req.getPriority() != null) {
				t.setDescription(req.getDescription());
			}
			if(req.getStatus() != null) {
				t.setStatus(req.getStatus());
			}
			
			//저장후 반환(컨트롤러에서 204/200 선택 예정)
			return taskRepository.save(t);
		}
		
		//taskId 삭제
		public void deleteTask(Long id) {
			Task t = taskRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Task를 찾을 수 없습니다. id=" + id));
			
			taskRepository.delete(t);
		}
		
		//userId = 사용자 ID -> 해당 사용자의 Task리스트 
		public List<Task> getTasksByUser(Long userId){
			return taskRepository.findByUserId(userId);
		}
}

package com.example.todoai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.todoai.entity.Task;
import com.example.todoai.repository.TaskRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {
		private final TaskRepository taskRepository;
		
		//task저장할 할 일 정보
		public Task createdTask(Task task) {
			return taskRepository.save(task);
		}
		
		//taskid 객체 조회 - 없으면 null
		public Task getTask(Long id) {
			return taskRepository.findById(id).orElse(null);
		}
		
		//task 수정
		public Task updateTask(Long id, Task updatedTask) {
			Task task = taskRepository.findById(id).orElse(null);
			if(task != null) {
				task.setTitle(updatedTask.getTitle());
				task.setDescription(updatedTask.getDescription());
				task.setPriority(updatedTask.getPriority());
				task.setStatus(updatedTask.getStatus());
				return taskRepository.save(task);
			}
			return null;
		}
		
		//taskId 삭제
		public void deleteTask(Long id) {
			taskRepository.deleteById(id);
		}
		
		//userId = 사용자 ID -> 해당 사용자의 Task리스트 
		public List<Task> getTasksByUser(Long userId){
			return taskRepository.findByUserId(userId);
		}
}

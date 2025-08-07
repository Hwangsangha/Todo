package com.example.todoai;

import org.springframework.stereotype.Component;

import com.example.todoai.entity.Priority;
import com.example.todoai.entity.Status;
import com.example.todoai.entity.Task;
import com.example.todoai.entity.User;
import com.example.todoai.service.TaskService;
import com.example.todoai.service.UserService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component	//클래스를 spring이 자동으로 빈으로 등록
@RequiredArgsConstructor	//생성자 주입을 Lombok이 자동 생성(final 필드 주입용)
public class TestDataRunner {
	
	//service 주입 (final로 선언하면 @RequiredArgsConstructor로 자동 주입)
	private final UserService userService;
	private final TaskService taskService;
	
	@PostConstruct	//이 메서드는 스프링 컨테이너가 빈 초기화한 후 자동 실행
	public void init() {
		
		try {
			//사용자 생성
			System.out.println("사용자 생성시작");
			User user = new User();
			user.setUsername("testuser");
			user.setEmail("test@example.com");
			user.setPassword("1234");
			User saveUser = userService.creatUser(user);
			
			//우선순위/상태 테스트 데이터
			System.out.println("Enum 값 지정");
			Priority priority = Priority.HIGH;
			Status status = Status.TODO;
			
			//할일 생성
			System.out.println("할 일 생성 시작");
			Task task = new Task();
			task.setTitle("할 일 예시");
			task.setDescription("예시 설명입니다.");
			task.setUser(saveUser);
			task.setPriority(priority);
			task.setStatus(status);
			
			taskService.createdTask(task);
			System.out.println("테스트 데이터 생성 완료");
			
		} catch(Exception e) {
			System.err.println("PostConstruct 실행 중 에러 발생:");
			e.printStackTrace();
		}
	}
}

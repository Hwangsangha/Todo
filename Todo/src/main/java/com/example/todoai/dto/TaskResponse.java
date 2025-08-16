package com.example.todoai.dto;

import com.example.todoai.entity.Priority;
import com.example.todoai.entity.Status;
import com.example.todoai.entity.Task;

//목적: Task엔티티를 직접 노출하지 않고 API 응답 전용데이터만 반환한다.
//주의: 엔티티에 없는 필드는 넣지 않는다.
public class TaskResponse {
	private Long id;
	private String title;
	private String description;
	private Priority priority;
	private Status status;
	private Long userId;	//연관관계는 식별자만 노출
	
	//엔티티 > 응답 DTO 변환 메서드
	public static TaskResponse from(Task t) {
		TaskResponse r = new TaskResponse();
		r.id = t.getId();
		r.title = t.getTitle();
		r.description = t.getDescription();
		r.priority = t.getPriority();
		r.status = t.getStatus();
		
		//연관된 User가 null이 아닌 경우에만 userId를 가져옴
		//목적: User 전체를 노출하지 않고, "소유자 식별자"만 API 응답에 포함
		r.userId = (t.getUser() != null) ? t.getUser().getId() : null;
		
		return r;
	}
	
	//getter만 제공 > 외부에서 읽기 전용으로 사용
	public Long getId() {return id;}
	public String getTitle() {return title;}
	public String getDescription() {return description;}
	public Priority getPriority() {return priority;}
	public Status getStatus() {return status;}
	public Long getUserId() {return userId;}
}

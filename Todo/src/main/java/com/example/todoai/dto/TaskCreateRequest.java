package com.example.todoai.dto;

import com.example.todoai.entity.Priority;
import com.example.todoai.entity.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TaskCreateRequest {

	@NotBlank(message = "title은 비어있을 수 없습니다.")
	@Size(max = 100, message = "title은 100자 이하여야 합니다.")
	private String title;
	
	@NotBlank(message = "description은 비어있을 수 없습니다.")
	@Size(max = 1000, message = "description은 1000자 이하여야 합니다.")
	private String description;
	
	@NotNull(message = "priority는 필수 입니다.")
	private Priority priority;
	
	@NotNull(message = "status는 필수 입니다.")
	private Status status;
	
	//선택 : yyyy-MM-dd로 받는다면 @Pattern 지정가능
	private String dueDate;
	
	public String getTitle() {return title;}
	public String getDescription() {return description;}
	public Priority getPriority() {return priority;}
	public Status getStatus() {return status;}
	public String getDueDate() {return dueDate;}
}

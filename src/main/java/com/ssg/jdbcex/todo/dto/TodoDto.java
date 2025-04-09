package com.ssg.jdbcex.todo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {
    private Long tno;
    private String title;
    private LocalDateTime dueDate;
    private boolean finished;
}

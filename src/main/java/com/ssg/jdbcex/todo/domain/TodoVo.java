package com.ssg.jdbcex.todo.domain;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TodoVo {
    private Long tno;
    private String title;
    private LocalDateTime dueDate;
    private boolean finished;
}

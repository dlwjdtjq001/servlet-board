package com.ssg.jdbcex.todo.service;

import com.ssg.jdbcex.todo.domain.TodoVo;
import com.ssg.jdbcex.todo.dto.TodoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class TodoServiceTest {
    private TodoService todoService;

    @BeforeEach
    public void setUp() {
        todoService = TodoService.INSTANCE;
    }

    public Supplier<TodoDto> supplier = () -> {
        TodoDto todo = TodoDto.builder()
                .tno(1L)
                .title("hi")
                .dueDate(LocalDateTime.now())
                .build();
        return todo;
    };

    @Test
    public void testRegister() throws SQLException {
        //given
        TodoDto todoDto = supplier.get();
        //when
        todoService.register(todoDto);
    }
}
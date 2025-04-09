package com.ssg.jdbcex.todo.controller;


import com.ssg.jdbcex.todo.Controller;
import com.ssg.jdbcex.todo.dto.TodoDto;
import com.ssg.jdbcex.todo.service.TodoService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class TodoRegisterController implements Controller {
    TodoService todoService = TodoService.INSTANCE;
    @Override
    public String process(Map<String, Object> paramMap, Map<String, Object> model) throws Exception {
        // POST 요청일 경우: 등록 처리 후 리다이렉트
        if (paramMap.containsKey("title")) { // 예: title이 들어왔으면 등록 요청
            return postRegister(paramMap, todoService);
        }
        // GET 요청일 경우: 등록 화면 보여주기
        return getRegister();
    }

    private static String getRegister() {
        return "register";
    }

    private static String postRegister(Map<String, Object> paramMap, TodoService todoService) throws SQLException {
        // TodoDto로 변환
        TodoDto dto = TodoDto.builder()
                .title((String) paramMap.get("title"))
                .dueDate(LocalDate.parse((String) paramMap.get("dueDate")).atStartOfDay()) // LocalDate → LocalDateTime
                .finished(false)
                .build();
        // 서비스에 저장
        todoService.register(dto);
        return "redirect:/todo/list";
    }

}

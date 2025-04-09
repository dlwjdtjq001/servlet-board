package com.ssg.jdbcex.todo.controller;


import com.ssg.jdbcex.todo.Controller;
import com.ssg.jdbcex.todo.dto.TodoDto;
import com.ssg.jdbcex.todo.service.TodoService;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Log4j2
public class TodoListController implements Controller {
    TodoService todoService = TodoService.INSTANCE;
    @Override
    public String process(Map<String, Object> paramMap, Map<String, Object> model) throws Exception {
        model.put("list",todoService.listAll());
        return "list";
    }
}

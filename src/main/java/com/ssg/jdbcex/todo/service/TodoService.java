package com.ssg.jdbcex.todo.service;


import com.ssg.jdbcex.todo.dao.TodoDao;
import com.ssg.jdbcex.todo.domain.TodoVo;
import com.ssg.jdbcex.todo.dto.TodoDto;
import com.ssg.jdbcex.todo.util.MapperUtil;
import com.sun.tools.javac.comp.Todo;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
public enum TodoService {
    INSTANCE;
    //todoDao와 service 연결

    private TodoDao dao;
    private ModelMapper modelMapper;

    TodoService(){
        dao = new TodoDao();
        modelMapper = MapperUtil.INSTANCE.getModelMapper();
    }

    public void register(TodoDto todoDto) throws SQLException {
        TodoVo todoVo = modelMapper.map(todoDto, TodoVo.class);
        log.info(todoVo);
        dao.insert(todoVo);
    }

//    public List<TodoDto> getList(){
//
//
//
//        List<TodoDto> todoDtoList = IntStream.range(0,10).mapToObj(i -> {
//            TodoDto todoDto = new TodoDto();
//            todoDto.setTno((long)i);
//            todoDto.setTitle("Todo.." + i);
//            todoDto.setDueDate(LocalDateTime.now());
//            return todoDto;
//        }).collect(Collectors.toList());
//        return todoDtoList;
//    }

//    public TodoDto get(Long tno){
//        TodoDto dto = new TodoDto();
//        dto.setTno(tno);
//        dto.setDueDate(LocalDateTime.now());
//        dto.setTitle("title");
//        dto.setFinished(true);
//        return dto;
//    }

    public List<TodoDto> listAll() throws Exception {
        List<TodoVo> voList = dao.selectAll();
        log.info(voList);
        List<TodoDto> dtos = voList.stream()
                .map(vo -> modelMapper.map(vo, TodoDto.class))
                .collect(Collectors.toList());
        return dtos;
    }
}

//enum 타입은 정해진 수만큼 객체를 생성할 수 있다.

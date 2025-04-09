package com.ssg.jdbcex.todo.dao;

import com.ssg.jdbcex.todo.domain.TodoVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class TodoDaoTest {
    private TodoDao todoDao;

    @BeforeEach
    public void ready(){
        todoDao = new TodoDao();
    }

    @Test
    public void testTime() throws Exception {
        String time = todoDao.getTime();
        assertNotNull(time, "DB에서 가져온 시간이 null이면 안 됩니다.");
        System.out.println("현재 시간: " + time);
    }

    @Test
    public void testTime2() throws Exception {
        String time = todoDao.getTime2();
        assertNotNull(time, "DB에서 가져온 시간이 null이면 안 됩니다.");
        System.out.println("현재 시간: " + time);
    }

    public Supplier<TodoVo> supplier = () -> {
        TodoVo todo = TodoVo.builder()
                .tno(1L)
                .title("hi")
                .dueDate(LocalDateTime.now())
                .build();
        return todo;
    };
    @Test
    public void testTime3() throws Exception {
        todoDao.insert(supplier.get());
    }

    @Test
    public void testList() throws Exception {
        todoDao.selectAll().forEach(System.out::println);
    }

    @Test
    public void testUpdate() throws Exception {
        // given
        todoDao.updateOne(supplier.get());

        TodoVo updatedVo = supplier.get();

        // 2. when
        todoDao.updateOne(updatedVo);
        TodoVo result = todoDao.selectAll().stream()
                .filter(vo -> vo.getTno() == 1L)
                .findFirst()
                .orElse(null);

        // then
        assertEquals(updatedVo.getTitle(), result.getTitle());
        assertEquals(updatedVo.getDueDate().toLocalDate(), result.getDueDate().toLocalDate()); // DATE만 비교
        assertEquals(updatedVo.isFinished(), result.isFinished());
    }
}
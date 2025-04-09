package com.ssg.jdbcex.todo.dao;

import com.ssg.jdbcex.todo.domain.TodoVo;
import lombok.Cleanup;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//db 로부터 시간 얻어오는 간단한 기능 구현
public class TodoDao {
    public String getTime(){
        String now = null;
        try(Connection connection = ConnectionUtil.INSTANCE.getConnection();
            PreparedStatement ps = connection.prepareStatement("select now()");
            ResultSet resultSet = ps.executeQuery()){
            resultSet.next();
            now = resultSet.getString(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return now;
    }

    public String getTime2() throws SQLException {
        @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
        @Cleanup PreparedStatement ps = connection.prepareStatement("select now()");
        @Cleanup ResultSet resultSet = ps.executeQuery();
        resultSet.next();
        return resultSet.getString(1);
    }

    //tbl_todo 테이블에 todo 를 넣는 insert (TodoVo vo)
    public void insert(TodoVo vo) throws SQLException {
        String sql = "insert into tbl_todo(title, dueDate, finished) values(?,?,?)";
        @Cleanup PreparedStatement ps = ConnectionUtil.INSTANCE.getConnection().prepareStatement(sql);
        ps.setString(1, vo.getTitle());
        ps.setDate(2, Date.valueOf(vo.getDueDate().toLocalDate()));
        ps.setBoolean(3, vo.isFinished());

        ps.executeUpdate();

    }

    public List<TodoVo> selectAll() throws Exception{
        String sql = "select * from tbl_todo";
        @Cleanup PreparedStatement ps = ConnectionUtil.INSTANCE.getConnection().prepareStatement(sql);
        @Cleanup ResultSet resultSet = ps.executeQuery();
        ArrayList<TodoVo> todoVos = new ArrayList<>();
        while(resultSet.next()){
            TodoVo todoVo = TodoVo.builder()
                    .tno(resultSet.getLong(1))
                    .title(resultSet.getString(2))
                    .dueDate(resultSet.getTimestamp(3).toLocalDateTime())
                    .finished(resultSet.getBoolean(4))
                    .build();
            todoVos.add(todoVo);
        }
        return todoVos;
    }

    public void deleteOne(Long tno) throws Exception{
        String sql = "delete from tbl_todo where tno = ?";
        @Cleanup PreparedStatement ps = ConnectionUtil.INSTANCE.getConnection().prepareStatement(sql);
        ps.setLong(1, tno);
        ps.executeUpdate();
    }

    public void updateOne(TodoVo todoVo) throws Exception{
        String sql = "update tbl_todo set title = ? , duedate = ? , finished = ? where tno = ?;";
        @Cleanup PreparedStatement ps = ConnectionUtil.INSTANCE.getConnection().prepareStatement(sql);
        ps.setString(1, todoVo.getTitle());
        ps.setTimestamp(2, Timestamp.valueOf(todoVo.getDueDate()));
        ps.setBoolean(3, todoVo.isFinished());
        ps.setLong(4, todoVo.getTno());
        ps.executeUpdate();
    }
}

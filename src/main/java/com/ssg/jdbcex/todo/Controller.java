package com.ssg.jdbcex.todo;

import java.util.Map;

public interface Controller {
    String process(Map<String, Object> paramMap , Map<String, Object> model) throws Exception;
}

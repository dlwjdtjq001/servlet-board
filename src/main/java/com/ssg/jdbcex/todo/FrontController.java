package com.ssg.jdbcex.todo;


import com.ssg.jdbcex.todo.controller.TodoListController;
import com.ssg.jdbcex.todo.controller.TodoRegisterController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "FrontController", urlPatterns = "/todo/*")
public class FrontController extends HttpServlet {
    private Map<String, Controller> controllerMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        controllerMap.put("/todo/list",new TodoListController());
        controllerMap.put("/todo/register", new TodoRegisterController());

    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        Controller controller = controllerMap.get(uri); // 이게 이제 잘 작동함

        Map<String, Object> paramMap = createParamMap(request);
        Map<String, Object> model = new HashMap<>();

        try {
            String viewName = controller.process(paramMap, model);

            if (viewName.startsWith("redirect:")) {
                String redirectPath = viewName.substring("redirect:".length());
                response.sendRedirect(redirectPath);
            } else {
                View view = new View("/WEB-INF/todo/" + viewName + ".jsp");
                view.render(model, request, response);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> createParamMap(HttpServletRequest request) {
        Map<String, Object> paramMap = new HashMap<>();
        Collections.list(request.getParameterNames())
                .forEach(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}

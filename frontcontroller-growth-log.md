
# 서블릿으로 시작한 웹 애플리케이션 개발, 그리고 프론트 컨트롤러로의 진화

## 🎯 처음엔 서블릿 하나에 다 몰아 넣었습니다

```java
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        String message = "Hello, World!";
        req.setAttribute("msg", message);
        req.getRequestDispatcher("/WEB-INF/index.jsp").forward(req, resp);
    }
}
```

## ❌ 점점 드러난 불편함

### 1) setAttribute + forward 코드 반복
JSP로 넘기기 위한 로직이 모든 서블릿에 반복되었습니다.

### 2) 모든 컨트롤러가 HttpServlet을 상속
비즈니스 로직과 무관한 HttpServlet 종속이 생기고 테스트가 어려워졌습니다.

### 3) request, response를 항상 넘겨야 함
사용하지 않아도 파라미터에 넣어야 했습니다.

## 🔁 그래서 프론트 컨트롤러를 도입했습니다

```java
@WebServlet("/todo/*")
public class FrontController extends HttpServlet {
    private Map<String, Controller> controllerMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        controllerMap.put("/todo/list", new TodoListController());
        controllerMap.put("/todo/read", new TodoReadController());
        // ...
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        String path = req.getPathInfo();
        Controller controller = controllerMap.get(path);

        if (controller == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        View view = controller.process(req, resp);
        view.render(req, resp);
    }
}
```

## 🔧 컨트롤러는 이렇게 분리했습니다

```java
public class TodoListController implements Controller {

    private TodoService todoService = TodoService.INSTANCE;

    @Override
    public View process(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        List<TodoVo> todoList = todoService.getList();
        req.setAttribute("list", todoList);
        return new View("/WEB-INF/todo/list.jsp");
    }
}
```

## 🔍 뷰는 View 클래스로 추상화

```java
public class View {
    private String path;

    public View(String path) {
        this.path = path;
    }

    public void render(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(path).forward(req, resp);
    }
}
```

## ✅ 무엇이 개선되었나요?

| 항목 | 서블릿 중심 구조 | 프론트 컨트롤러 구조 |
|------|------------------|----------------------|
| 코드 중복 | 많음 (`setAttribute`, `forward`) | 뷰 처리 추상화로 중복 제거 |
| 테스트 용이성 | 낮음 (`HttpServlet` 상속) | 높음 (POJO 컨트롤러 사용) |
| 책임 분리 | 부족함 | 명확함 (`FrontController`, `Controller`, `View`) |
| 유지보수성 | 낮음 | 높음 |
| URL 매핑 관리 | 서블릿마다 별도 설정 | 중앙 집중 방식 (`Map` 기반 매핑) |

## 🧠 나의 성장, 그 흔적

처음엔 단순한 서블릿으로 시작했지만, 확장성과 유지보수를 고려하면서 구조적인 개선이 필요하다는 것을 깨달았습니다. 프론트 컨트롤러 패턴을 도입함으로써 책임 분리를 실현하고, 테스트 가능한 코드로 나아가며 개발자로서 한층 성장했습니다.

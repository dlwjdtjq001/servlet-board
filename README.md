
# 서블릿으로 게시판을 만들던 중 불편함을 느꼈다

기존의 콘솔 프로그램을 웹에서 동작하게 하기 위해 서블렛을 공부하던 중 간단하게 게시판을 만들어 보고자 하였다. 간단하게 작성, 리스트, 삭제, 수정을 가지고 있는 게시판을 계획하였고 완성된 구조는 다음과 같았다. 
![](https://velog.velcdn.com/images/dlwjdtjq1234/post/36e2210b-2785-41f0-9842-05d1a9a179f9/image.png)


## V1 구조 :

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

## 느꼈던 문제점 불편함

### 1) setAttribute + forward 코드 반복
JSP로 넘기기 위한 로직이 모든 서블릿에 반복된다. 그리고 url값도 직접 모든 컨트롤러에 넣는 작업을 반복해야 했다. 

### 2) 모든 컨트롤러가 HttpServlet을 상속
컨트롤러에선 비지니스 로직만을 처리하고 싶은데 모든 컨트롤러가 HttpServlet을 Extends 해야하는 상황이 생겼다. 이는 다른 객체를 상속할수도 없게 할 뿐 아니라(거의 없을것 같긴 하다.) 실제 프로젝트에서 TDD형태로 개발을 할 시에 서블릿에 종속되는 결과가 생긴다. 이를 해결하고 싶었다.

### 3) request, response를 항상 넘겨야 함
실제로 컨트롤러에서 request, response 객체를 쓰지않는 컨트롤러도 있었지만 굳이 다 파라미터로 넘겨 받아야 하는 상황이 생겼다.

## 자료수집

위 불편함을 해결하기 위해 자료 검색을 했다. 
하던 와중 프론트 컨트롤러의 도입과 뷰 리졸버의 존재를 알게 되었고 해당 내용을 이해하기위해 더 찾아봤다. 힌트는 스프링에 있었다.
![](https://velog.velcdn.com/images/dlwjdtjq1234/post/4afd51e0-9ff8-46c2-becc-57312c5777de/image.png)

먼저 모든 컨트롤러는 맵핑하고 가져오는 핸들러 맵퍼를 통해 해당하는 핸들러를 가져 온 뒤에 그 핸들러를 호출한다.
모델과 뷰 값을를 컨트롤러에서 생성하고 뷰 리졸버와 컨트롤러에서 얻은 뷰 값을 통해 해당 주소를 얻어와서 최종적으로 뷰에서 jsp를 사용하게 된다.

이를 서블렛으로 구현하면 위에서 느꼈던 문제점이 해결될 것 같았다.

## 해야할것

- 모든 요청을 받을 수 있는 프론트 컨트롤러를 만들자
- 미리 진짜 사용할 컨트롤러들의 정보를 핸들로맵핑을 통해 가져오자
- 컨트롤러에서 모델과 뷰값을 넘겨주자
- 뷰 리졸버를 통해 실제 주소값을 받아서 jsp를 통해 출력하자

이 네가지로 목표를 두고 리펙토링 하기로 했다.


## V2. 프론트 컨트롤러 생성


```java
@WebServlet(name = "FrontController", urlPatterns = "/todo/*")
public class FrontController extends HttpServlet {
    }
```

모든 todo 요청에 대해 처리할 프론트 컨트롤러이다. 여기에서 모든 작업을 처리할 예정이기 때문에 /todo/* 로 패턴을 넣었다.

## V3. 핸들러 맵핑

```java
private Map<String, Controller> controllerMap = new HashMap<>();

 @Override
    public void init() throws ServletException {
        controllerMap.put("/todo/list",new TodoListController());
        controllerMap.put("/todo/register", new TodoRegisterController());
        controllerMap.put("/todo/update", new TodoUppdateController());
        controllerMap.put("/todo/delete", new TodoDeleteController());

    }
    
    
public interface Controller {
    String process(Map<String, Object> paramMap , Map<String, Object> model) throws Exception;
}

```

컨트롤러 인터페이스를 하나 두고 해당 인터페이스를 상속받는 실제 컨트롤러들을 맵핑한다. 
그릐고 초기화는 프론트 컨트롤러가 init 할떄 시작하도록 설계하였다.

paramMap에는 받아온 request를 통해 넘어온 값들을, model에는 넘길 값들을 넣어줄 것이다. 

**여기서 내가 설계한 프론트 컨트롤러와 위의 스프링 mvc의 다른점이 나온다.**

스프링 mvc에서는 컨트롤러에서 ModelAndView로 뷰값과 모델값을 둘다 가지고 있는 객체를 만들어 넘기게 되는데 두가지 역할을 하는게 맘에 안들었다. 따라서 모델은 프론트 컨트롤러에서 만들고 컨트롤러 안에서 파라미터로 받은 모델을 조작할 수 있게 하였다. 
이렇게 되면 컨트롤러는 단지 뷰값만 넘기면 되게 된다. 

```java
		Map<String, Object> paramMap = createParamMap(request);
        Map<String, Object> model = new HashMap<>();

        try {
            String viewName = controller.process(paramMap, model);
            
            

@Log4j2
public class TodoListController implements Controller {
    TodoService todoService = TodoService.INSTANCE;
    @Override
    public String process(Map<String, Object> paramMap, Map<String, Object> model) throws Exception {
        model.put("list",todoService.listAll());
        return "list";
    }
}

```





## V4. View 리졸버


위에서 (V1) 본 컨트롤러는 또 하나의 문제점이 있었다. 바로 전체 uri 값을 모두 넘기게 되는데 이는 수많은 컨트롤러가 생길걸 생각하면 상당히 비효율 적이였다. 따라서 이를 논리주소와 실제주소를 맵핑 할수 있게 하여 따로 작업을 해 놓으면 컨트롤러에서는 단지 논리주소만 넘기고 프론트 컨트롤러가 뷰 리졸버와 뷰를 통해 포워딩 까지 하게 되는 모습으로 설계할 수 있을 것 같았다. 

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



public String process(Map<String, Object> paramMap, Map<String, Object> model) throws Exception {
        model.put("list",todoService.listAll());
        return "list";
    }
```
결국 컨트롤러는 서비스 호출, 논리주소 반환 두줄이면 간단하게 끝나는 구조가 되었고이는 확장할떄 꽤 많은 수요를 줄일 수 있다.






## 개선점

| 항목 | 서블릿 중심 구조 | 프론트 컨트롤러 구조 |
|------|------------------|----------------------|
| 코드 중복 | 많음 (`setAttribute`, `forward`) | 뷰 처리 추상화로 중복 제거 |
| 테스트 용이성 | 낮음 (`HttpServlet` 상속) | 높음 (POJO 컨트롤러 사용) |
| 책임 분리 | 부족함 | 명확함 (`FrontController`, `Controller`, `View`) |
| 유지보수성 | 낮음 | 높음 |
| URL 매핑 관리 | 서블릿마다 별도 설정 | 중앙 집중 방식 (`Map` 기반 매핑) |

## V5. 하지만.... 해결해야할 문제

해당 리펙토링을 하는 과정 중에 문제가 발생했다. 
핸들러 맵핑을 하는 과정에서 컨트롤러는 하나의 프로세스만 가지게 되었다. 
하지만 같은 uri 에서 post, get 방식으로 부를 수 있는 상황이 생겼다. 
원래 구조는 doget, dopost 로 분기하지만 난 컨트롤러를 임플리먼트 하기때문에 사용할 수 없었다. 따라서 프로세스 안에서 doget, dopost 를 판별하여 작업하기로 하였다. 
```java

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
```

프로세스 안에서 post 와 get 을 분기하고 해당 작업을 처리한다. 그리고 포스트일떄는 list로 redirect 해야 하기 떄문에 return 에 redirect 를 추가하고 프론트 컨트롤러에서 캐치하여 작업하는 방식을 생각했다. 



```java
String viewName = controller.process(paramMap, model);

            if (viewName.startsWith("redirect:")) {
                String redirectPath = viewName.substring("redirect:".length());
                response.sendRedirect(redirectPath);
            } else {
                View view = new View("/WEB-INF/todo/" + viewName + ".jsp");
                view.render(model, request, response);
```

프론트 컨트롤러의 모습이다. 

if문으로 받아서 어떤 방식으로 처리할지 나누었다. 
이러면 컨트롤러가 보낸 의도를 파악하여 프론트 컨트롤러가 해당하는 작업을 할 수 있게 되었다. 

## 추가 개선해야 할 점
### 1) post 와 get 의 판별
현재는 타이틀 값이 해당하는 경우에 포스트, 아니면 겟으로 프로세스를 짰는데 이는 상당히 불안정하다. 따라서 컨트롤러의 파라미터로 request 를 같이 넘기고 getmethod 를 통해 post 방식인지 겟방식인지 직접적으로 확인하는게 좋을것 같다. 

### 2) 프론트 컨트롤러의 if문
컨트롤러의 작업 방식이 많아지게 되면 그에따라 이프문이 많아지게 되고 이는 명시적이지 않다. 따라서 이를 함수형으로 뺴고 맵에 맵핑하여 Map.get으로 해당 Runnable.run 메서드참조를 하게 하면 명시적이고 알아보기도 쉬운 코드가 될 것 같다. 

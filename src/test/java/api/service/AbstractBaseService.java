package api.service;

import io.restassured.specification.RequestSpecification;
import lombok.Getter;

@Getter
public abstract class AbstractBaseService<T>{
    private final RequestSpecification requestSpecification;

    // Тут же храним повторяющиеся кусочки эндпоинтов, чтобы делать минимум изменений в сервисном классе,
    // если адрес будет меняться
    private final String api = "api";

    // Не используем тут аннотацию @RequiredArgConstructor, потому что она подхватывает все final переменные класса,
    // а нам надо только requestSpecification
    protected AbstractBaseService(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;


        // сюда же можно вынести и штуки, закомментированные ниже. Не делаю этого, чтоб не поломать тест Олега,
        // которые использую getDefaultRequestSpec(). А там все те же самые настройки

//        requestSpecification.contentType(ContentType.JSON);
//        requestSpecification.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
//        requestSpecification.log().all();
    }
}

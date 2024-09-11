package swagertest;

import lombok.val;
import models.FullUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import service.UserService;

import static assertions.Conditions.*;
import static service.ServiceManager.*;
import static utils.TestData.getAdmin;
import static utils.TestData.getRandomUser;
import static utils.constants.ResponseErrors.UNAUTHORIZED;
import static utils.constants.ResponseMessages.*;
import static utils.constants.ResponseMessages.CREATED_USER_MESSAGE;
import static utils.constants.ResponseMessages.LOGIN_EXIST_MESSAGE;

@Tag("OlegTests")
public class UserNewTests {

    private static final UserService userService = new UserService();
    private FullUser user;

    @BeforeEach
    public void initTestUser(){
        user = getRandomUser();
    }

    @Test
    void positiveRegisterTest(){
        userService.register(user)
                .should(hasStatusCode(200))
//                .should(hasMessage(CREATED_USER_MESSAGE))
                // Написали с Таней. Это проверка целиком блока info из ответа
                .should(hasInfo(CREATED_USER_MESSAGE, SUCCESS_STATUS));
    }

    // Переписали с Таней. Не очень оптимально
    @Test
    void positiveRegisterTest2(){
        getUserService().register(user)
                .should(hasStatusCode(201))
                .should(hasInfo(CREATED_USER_MESSAGE, SUCCESS_STATUS));
    }

    // Переписали с Таней. Оптимально. Отличается от предыдущего другим вызовом сервиса
    // Тут более грамотно передается спецификация внутри getUserService2()
    @Test
    void positiveRegisterTest3(){
        getUserServiceTanya().register(user)
                .should(hasStatusCode(201))
                .should(hasInfo(CREATED_USER_MESSAGE, SUCCESS_STATUS));
    }

    @Test
    void negativeRegisterLoginExistTest() {
        userService.register(user);
        userService.register(user)
//                .should(hasMessage(LOGIN_EXIST_MESSAGE))
                // Дописал сам по примеру сделанного с Таней
                .should(hasInfo(LOGIN_EXIST_MESSAGE, FAIL_STATUS));
    }

    @Test
    public void negativeRegisterNoPasswordTest() {
        user.setPass(null);
        userService.register(user)
                .should(hasStatusCode(400))
//                .should(hasMessage(MISSING_LOGIN_OR_PASSWORD_MESSAGE))
                // Дописал сам по примеру сделанного с Таней
                .should(hasInfo(MISSING_LOGIN_OR_PASSWORD_MESSAGE, FAIL_STATUS));
    }

    @Test
    public void positiveAdminAuthTest() {
        String token = userService.auth(getAdmin())
                .should(hasStatusCode(200))
                .asToken();

        Assertions.assertNotNull(token);
    }

    // Через извлечение токена
    @Test
    public void positiveNewUserAuthTest1() {
        userService.register(user);
        String token = userService.auth(user)
                .should(hasStatusCode(200))
                .asToken();

        Assertions.assertNotNull(token);
    }

    // Проверка под капотом
    @Test
    public void positiveNewUserAuthTest2() {
        userService.register(user);
        userService.auth(user)
                .should(hasStatusCode(200))
                .should(hasNotEmptyToken());
    }

    @Test
    public void negativeAuthTest() {
        userService.auth(user)
                .should(hasStatusCode(401))
                .should(hasError(UNAUTHORIZED));
    }

    @Test
    public void positiveGetUserInfoTest(){
        val token = userService.auth(getAdmin())
                .asToken();
        userService.getUserInfo(token)
                .should(hasStatusCode(200))
                .should(hasNoEmptyValues());
    }
}

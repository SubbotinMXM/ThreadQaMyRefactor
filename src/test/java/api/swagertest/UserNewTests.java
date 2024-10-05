package api.swagertest;

import api.assertions.Conditions;
import api.models.FullUser;
import api.service.ServiceManager;
import api.service.UserService;
import api.utils.TestData;
import api.utils.constants.ResponseErrors;
import api.utils.constants.ResponseMessages;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("OlegTests")
public class UserNewTests {

    private static final UserService userService = new UserService();
    private FullUser user;

    @BeforeEach
    public void initTestUser(){
        user = TestData.getRandomUser();
    }

    @Test
    void positiveRegisterTest(){
        userService.register(user)
                .should(Conditions.hasStatusCode(200))
//                .should(hasMessage(CREATED_USER_MESSAGE))
                // Написали с Таней. Это проверка целиком блока info из ответа
                .should(Conditions.hasInfo(ResponseMessages.CREATED_USER_MESSAGE, ResponseMessages.SUCCESS_STATUS));
    }

    // Переписали с Таней. Не очень оптимально
    @Test
    void positiveRegisterTest2(){
        ServiceManager.getUserService().register(user)
                .should(Conditions.hasStatusCode(201))
                .should(Conditions.hasInfo(ResponseMessages.CREATED_USER_MESSAGE, ResponseMessages.SUCCESS_STATUS));
    }

    // Переписали с Таней. Оптимально. Отличается от предыдущего другим вызовом сервиса
    // Тут более грамотно передается спецификация внутри getUserService2()
    @Test
    void positiveRegisterTest3(){
        ServiceManager.getUserServiceTanya().register(user)
                .should(Conditions.hasStatusCode(201))
                .should(Conditions.hasInfo(ResponseMessages.CREATED_USER_MESSAGE, ResponseMessages.SUCCESS_STATUS));
    }

    @Test
    void negativeRegisterLoginExistTest() {
        userService.register(user);
        userService.register(user)
//                .should(hasMessage(LOGIN_EXIST_MESSAGE))
                // Дописал сам по примеру сделанного с Таней
                .should(Conditions.hasInfo(ResponseMessages.LOGIN_EXIST_MESSAGE, ResponseMessages.FAIL_STATUS));
    }

    @Test
    public void negativeRegisterNoPasswordTest() {
        user.setPass(null);
        userService.register(user)
                .should(Conditions.hasStatusCode(400))
//                .should(hasMessage(MISSING_LOGIN_OR_PASSWORD_MESSAGE))
                // Дописал сам по примеру сделанного с Таней
                .should(Conditions.hasInfo(ResponseMessages.MISSING_LOGIN_OR_PASSWORD_MESSAGE, ResponseMessages.FAIL_STATUS));
    }

    @Test
    public void positiveAdminAuthTest() {
        String token = userService.auth(TestData.getAdmin())
                .should(Conditions.hasStatusCode(200))
                .asToken();

        Assertions.assertNotNull(token);
    }

    // Через извлечение токена
    @Test
    public void positiveNewUserAuthTest1() {
        userService.register(user);
        String token = userService.auth(user)
                .should(Conditions.hasStatusCode(200))
                .asToken();

        Assertions.assertNotNull(token);
    }

    // Проверка под капотом
    @Test
    public void positiveNewUserAuthTest2() {
        userService.register(user);
        userService.auth(user)
                .should(Conditions.hasStatusCode(200))
                .should(Conditions.hasNotEmptyToken());
    }

    @Test
    public void negativeAuthTest() {
        userService.auth(user)
                .should(Conditions.hasStatusCode(401))
                .should(Conditions.hasError(ResponseErrors.UNAUTHORIZED));
    }

    @Test
    public void positiveGetUserInfoTest(){
        val token = userService.auth(TestData.getAdmin())
                .asToken();
        userService.getUserInfo(token)
                .should(Conditions.hasStatusCode(200))
                .should(Conditions.hasNoEmptyValues());
    }
}

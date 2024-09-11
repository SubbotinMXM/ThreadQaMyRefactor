package swagertest;

import extensions.AdminUser;
import extensions.AdminUserResolver;
import extensions.RandomUser;
import extensions.RandomUserResolver;
import lombok.val;
import models.FullUser;
import org.awaitility.core.ConditionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static assertions.Conditions.*;
import static helper.RequestHelper.getDefaultRequestSpec;
import static java.util.concurrent.TimeUnit.*;
import static org.awaitility.Awaitility.await;
import static service.ServiceManager.getUserServiceTanya;
import static service.ServiceManager.getUserServiceTanya2;
import static utils.TestData.*;
import static utils.constants.ResponseErrors.UNAUTHORIZED;
import static utils.constants.ResponseMessages.*;

@ExtendWith({
        AdminUserResolver.class,
        RandomUserResolver.class
})
@Tag("TanyaTests")
public class UserNewTestsTanya {

    //  Влипил эвэйтилити просто как пример, чтоб было
    private static final ConditionFactory WAIT = await()
            .atMost(60, SECONDS)
            .pollInterval(1, SECONDS);

    private FullUser user;

    @BeforeEach
    public void initTestUser() {
        user = getRandomUser();
    }

    @Test
    void positiveRegisterTest() {
        getUserServiceTanya().register(user)
                .should(hasStatusCode(201))
                .should(hasInfo(CREATED_USER_MESSAGE, SUCCESS_STATUS));
    }

    // На работе на ППРБ у нас сделано вот так.
    // getUserServiceTanya2 принимает на вход спеку прямо в тесте. Там так сделано, тк мы используем
    // захардкоженную спеку из библиотеки "сценаристов", а сам тестовый класс наследуется от абстрктного класса,
    // который эту спеку и описывает
    @Test
    void positiveRegisterTest2() {
        getUserServiceTanya2(getDefaultRequestSpec()).register(user)
                .should(hasStatusCode(201))
                .should(hasInfo(CREATED_USER_MESSAGE, SUCCESS_STATUS));
    }

    //  Влипил эвэйтилити просто как пример, чтоб было
    @Test
    void negativeRegisterLoginExistTest() {
        getUserServiceTanya().register(user);
        WAIT.untilAsserted(() -> {
            getUserServiceTanya().register(user)
                    .should(hasInfo(LOGIN_EXIST_MESSAGE, FAIL_STATUS));
        });
    }

    @Test
    public void negativeRegisterNoPasswordTest() {
        user.setPass(null);
        getUserServiceTanya().register(user)
                .should(hasStatusCode(400))
                .should(hasInfo(MISSING_LOGIN_OR_PASSWORD_MESSAGE, FAIL_STATUS));
    }

    @Test
    public void positiveAdminAuthTest() {
        getUserServiceTanya().auth(getAdmin())
                .should(hasStatusCode(200))
                .should(hasNotEmptyToken());
    }

    // То что что и тест выше, только с экстеншеном для админа. Чисто для примера
    @Test
    public void positiveAdminAuthTest2(@AdminUser FullUser admin) {
        getUserServiceTanya().auth(admin)
                .should(hasStatusCode(200))
                .should(hasNotEmptyToken());
    }

    @Test
    public void positiveNewUserAuthTest() {
        getUserServiceTanya().register(user);
        getUserServiceTanya().auth(user)
                .should(hasStatusCode(200))
                .should(hasNotEmptyToken());
    }

    // То что что и тест выше, только с экстеншеном для рандомного пользака. Чисто для примера
    @Test
    public void positiveNewUserAuthTest2(@RandomUser FullUser randomUser) {
        getUserServiceTanya().register(randomUser);
        getUserServiceTanya().auth(randomUser)
                .should(hasStatusCode(200))
                .should(hasNotEmptyToken());
    }

    @Test
    public void negativeAuthTest() {
        getUserServiceTanya().auth(user)
                .should(hasStatusCode(401))
                .should(hasError(UNAUTHORIZED));
    }

    @Test
    public void positiveGetUserInfoTest() {
        val token = getUserServiceTanya().auth(getAdmin())
                .asToken();
        getUserServiceTanya().getUserInfo(token)
                .should(hasStatusCode(200))
                //отсебятинская проверка
                .should(hasNoEmptyValues());
    }

    @Test
    public void negativeGetUserInfoInvalidJwtTest() {
        getUserServiceTanya().getUserInfo(generateRandomString())
                .should(hasStatusCode(401));
    }

    @Test
    public void negativeGetUserInfoWithoutJwtTest() {
        getUserServiceTanya().getUserInfo()
                .should(hasStatusCode(401))
                .should(hasError(UNAUTHORIZED));
    }

    // Переписать с Таней
    @Test
    public void positiveChangeUserPassTest() {
        val oldPassword = user.getPass();
        val newPassword = "newPassword";

        getUserServiceTanya().register(user);
        var token = getUserServiceTanya()
                .auth(user)
                .asToken();

        getUserServiceTanya().updatePass(token, newPassword)
                .should(hasStatusCode(200))
                .should(hasMessage(PASSWORD_UPDATE));

        user.setPass(newPassword);

        token = getUserServiceTanya().auth(user)
                .should(hasStatusCode(200))
                .asToken();

        val updatedUser = getUserServiceTanya().getUserInfo(token).as(FullUser.class);

        Assertions.assertNotEquals(oldPassword, updatedUser.getPass());
    }

    // Проверка на невозможность изменить пароль админу
    @Test
    public void negativeChangeAdminPasswordTest() {
        val token = getUserServiceTanya().auth(getAdmin()).asToken();
        val updatedPass = "newPass";
        getUserServiceTanya().updatePass(token, updatedPass)
                .should(hasStatusCode(400))
                .should(hasInfo(PASSWORD_UPDATE_FAIL, FAIL_STATUS));
    }
}

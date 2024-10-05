package api.swagertest;

import api.assertions.Conditions;
import api.extensions.AdminUserResolver;
import api.utils.TestData;
import api.utils.constants.ResponseErrors;
import api.utils.constants.ResponseMessages;
import api.extensions.AdminUser;
import api.extensions.RandomUser;
import api.extensions.RandomUserResolver;
import lombok.val;
import api.models.FullUser;
import org.awaitility.core.ConditionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static api.helper.RequestHelper.getDefaultRequestSpec;
import static java.util.concurrent.TimeUnit.*;
import static org.awaitility.Awaitility.await;
import static api.service.ServiceManager.getUserServiceTanya;
import static api.service.ServiceManager.getUserServiceTanya2;

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
        user = TestData.getRandomUser();
    }

    @Test
    void positiveRegisterTest() {
        getUserServiceTanya().register(user)
                .should(Conditions.hasStatusCode(201))
                .should(Conditions.hasInfo(ResponseMessages.CREATED_USER_MESSAGE, ResponseMessages.SUCCESS_STATUS));
    }

    // На работе на ППРБ у нас сделано вот так.
    // getUserServiceTanya2 принимает на вход спеку прямо в тесте. Там так сделано, тк мы используем
    // захардкоженную спеку из библиотеки "сценаристов", а сам тестовый класс наследуется от абстрктного класса,
    // который эту спеку и описывает
    @Test
    void positiveRegisterTest2() {
        getUserServiceTanya2(getDefaultRequestSpec()).register(user)
                .should(Conditions.hasStatusCode(201))
                .should(Conditions.hasInfo(ResponseMessages.CREATED_USER_MESSAGE, ResponseMessages.SUCCESS_STATUS));
    }

    //  Влипил эвэйтилити просто как пример, чтоб было
    @Test
    void negativeRegisterLoginExistTest() {
        getUserServiceTanya().register(user);
        WAIT.untilAsserted(() -> {
            getUserServiceTanya().register(user)
                    .should(Conditions.hasInfo(ResponseMessages.LOGIN_EXIST_MESSAGE, ResponseMessages.FAIL_STATUS));
        });
    }

    @Test
    public void negativeRegisterNoPasswordTest() {
        user.setPass(null);
        getUserServiceTanya().register(user)
                .should(Conditions.hasStatusCode(400))
                .should(Conditions.hasInfo(ResponseMessages.MISSING_LOGIN_OR_PASSWORD_MESSAGE, ResponseMessages.FAIL_STATUS));
    }

    @Test
    public void positiveAdminAuthTest() {
        getUserServiceTanya().auth(TestData.getAdmin())
                .should(Conditions.hasStatusCode(200))
                .should(Conditions.hasNotEmptyToken());
    }

    // То что и тест выше, только с экстеншеном для админа. Чисто для примера. Здесь нас не интересует beforeEach
    @Test
    public void positiveAdminAuthTest2(@AdminUser FullUser admin) {
        getUserServiceTanya().auth(admin)
                .should(Conditions.hasStatusCode(200))
                .should(Conditions.hasNotEmptyToken());
    }

    @Test
    public void positiveNewUserAuthTest() {
        getUserServiceTanya().register(user);
        getUserServiceTanya().auth(user)
                .should(Conditions.hasStatusCode(200))
                .should(Conditions.hasNotEmptyToken());
    }

    // То что и тест выше, только с экстеншеном для рандомного пользака. Чисто для примера. Здесь нас не интересует beforeEach
    @Test
    public void positiveNewUserAuthTest2(@RandomUser FullUser randomUser) {
        getUserServiceTanya().register(randomUser);
        getUserServiceTanya().auth(randomUser)
                .should(Conditions.hasStatusCode(200))
                .should(Conditions.hasNotEmptyToken());
    }

    @Test
    public void negativeAuthTest() {
        getUserServiceTanya().auth(user)
                .should(Conditions.hasStatusCode(401))
                .should(Conditions.hasError(ResponseErrors.UNAUTHORIZED));
    }

    @Test
    public void positiveGetUserInfoTest() {
        val token = getUserServiceTanya().auth(TestData.getAdmin())
                .asToken();
        getUserServiceTanya().getUserInfo(token)
                .should(Conditions.hasStatusCode(200))
                //отсебятинская проверка
                .should(Conditions.hasNoEmptyValues());
    }

    @Test
    public void negativeGetUserInfoInvalidJwtTest() {
        getUserServiceTanya().getUserInfo(TestData.generateRandomString())
                .should(Conditions.hasStatusCode(401));
    }

    @Test
    public void negativeGetUserInfoWithoutJwtTest() {
        getUserServiceTanya().getUserInfo()
                .should(Conditions.hasStatusCode(401))
                .should(Conditions.hasError(ResponseErrors.UNAUTHORIZED));
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
                .should(Conditions.hasStatusCode(200))
                .should(Conditions.hasMessage(ResponseMessages.PASSWORD_UPDATE));

        user.setPass(newPassword);

        token = getUserServiceTanya().auth(user)
                .should(Conditions.hasStatusCode(200))
                .asToken();

        val updatedUser = getUserServiceTanya().getUserInfo(token).as(FullUser.class);

        Assertions.assertNotEquals(oldPassword, updatedUser.getPass());
    }

    // Проверка на невозможность изменить пароль админу
    @Test
    public void negativeChangeAdminPasswordTest() {
        val token = getUserServiceTanya().auth(TestData.getAdmin()).asToken();
        val updatedPass = "newPass";
        getUserServiceTanya().updatePass(token, updatedPass)
                .should(Conditions.hasStatusCode(400))
                .should(Conditions.hasInfo(ResponseMessages.PASSWORD_UPDATE_FAIL, ResponseMessages.FAIL_STATUS));
    }
}

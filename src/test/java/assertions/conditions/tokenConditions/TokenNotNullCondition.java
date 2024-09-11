package assertions.conditions.tokenConditions;

import assertions.Condition;
import io.restassured.response.ValidatableResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import models.Token;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TokenNotNullCondition implements Condition {

    @Override
    public void check(ValidatableResponse validatableResponse) {
        val token = validatableResponse.extract().as(Token.class);
        step("Проверка токена", () ->
            assertThat(token.getToken())
                    .withFailMessage("Ожидалось, что токен будет не пустой")
                    .isNotNull()
        );
    }
}

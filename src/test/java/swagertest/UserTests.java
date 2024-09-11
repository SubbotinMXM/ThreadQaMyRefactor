package swagertest;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.FullUser;
import models.Info;
import models.JwtAuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

@Tag("OldTests")
public class UserTests {

    private static Random random;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
        random = new Random();
    }


    // Проверка на успешную регистрацию пользака
    @Test
    public void positiveRegisterTest() {
        int randomNumber = Math.abs(random.nextInt());

        FullUser user = FullUser.builder()
                .login("threadQaTestUser!!! " + randomNumber)
                .pass("123456")
                .build();

        Info info = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("User created", info.getMessage());
    }

    // Создаем нового пользака, а потом пытаемся зарегать его под тем же логином. И проверяем ошибку
    @Test
    public void negativeRegisterLoginExistTest() {
        int randomNumber = Math.abs(random.nextInt());

        FullUser user = FullUser.builder()
                .login("threadQaTestUser!!! " + randomNumber)
                .pass("123456")
                .build();

        Info info = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("User created", info.getMessage());

        Info errorInfo = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("Login already exist", errorInfo.getMessage());
    }

//    // Проверяем ошибку при попытке зарегать пользака без пароля
//    // Конкретно в этом тесте много наворотов с обертками. Пока что нихера не понимаю
//    @Test
//    public void negativeRegisterNoPasswordTest() {
//        int randomNumber = Math.abs(random.nextInt());
//
//        FullUser user = FullUser.builder()
//                .login("threadQaTestUser!!! " + randomNumber)
//                .build();
//
//        Info info = given()
//                .contentType(ContentType.JSON)
//                .body(user)
//                .post("/api/signup")
//                .then()
//                .statusCode(400)
//                .extract().jsonPath().getObject("info", Info.class);
//
//        new AssertableResponse(given()
//                .contentType(ContentType.JSON)
//                .body(user)
//                .post("/api/signup")
//                .then())
//                .should(hasMessage("Missing login or password"))
//                .should(hasStatusCode(400));
//
//        new GenericAssertableResponse<Info>(given()
//                .contentType(ContentType.JSON)
//                .body(user)
//                .post("/api/signup")
//                .then(), new TypeRef<Info>() {})
//                .should(hasMessage("Missing login or password"))
//                .should(hasStatusCode(400))
//                        .asObject().getMessage();
//
//        Assertions.assertEquals("Missing login or password", info.getMessage());
//    }

    // Тест на успешный логин админом (захардкоженные имя и пароль)
    @Test
    public void positiveAdminAuthTest() {
        JwtAuthData jwtAuthData = new JwtAuthData("admin", "admin");

        String token = given()
                .contentType(ContentType.JSON)
                .body(jwtAuthData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
    }

    // Тест на успешный логин только что созданного пользака и что в ответ после авторизации прилетает какой-то токен
    @Test
    public void positiveNewUserAuthTest() {
        int randomNumber = Math.abs(random.nextInt());

        FullUser user = FullUser.builder()
                .login("threadQaTestUser!!! " + randomNumber)
                .pass("123456")
                .build();

        Info info = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("User created", info.getMessage());

        JwtAuthData jwtAuthData = new JwtAuthData(user.getLogin(), user.getPass());

        String token = given()
                .contentType(ContentType.JSON)
                .body(jwtAuthData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
    }

    // Негативный тест на авторизацию. Что нельзя авторизоваться с данными "от балды"
    @Test
    public void negativeAuthTest() {
        JwtAuthData jwtAuthData = new JwtAuthData("sdoijfdsj", "sdknfknjsd");

        given()
                .contentType(ContentType.JSON)
                .body(jwtAuthData)
                .post("/api/login")
                .then()
                .statusCode(401);
    }

    // Тест на получение инфы о юзере с использованием токена
    @Test
    public void positiveGetUserInfoTest(){
        JwtAuthData jwtAuthData = new JwtAuthData("admin", "admin");

        String token = given()
                .contentType(ContentType.JSON)
                .body(jwtAuthData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);

        given()
                .auth().oauth2(token)
                .get("/api/user")
                .then()
                .statusCode(200);

    }

    // Тест на попытку получить инфо о юзере по невалидному токену
    @Test
    public void negativeGetUserInfoInvalidJwtTest(){
        given()
                .auth().oauth2("token sdjfnapodsjnfakw")
                .get("/api/user")
                .then()
                .statusCode(401);
    }

    @Test
    public void negativeGetUserInfoWithoutJwtTest(){
        given()
                .get("/api/user")
                .then()
                .statusCode(401);
    }

    // Проверка на смену пароля. Что новый пароль не совпадает со старым
    @Test
    public void positiveChangeUserPassTest(){
        int randomNumber = Math.abs(random.nextInt());
        FullUser user = FullUser.builder()
                .login("threadQaTestUser!!! " + randomNumber)
                .pass("123456")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(201);

        JwtAuthData jwtAuthData = new JwtAuthData(user.getLogin(), user.getPass());

        String token = given()
                .contentType(ContentType.JSON)
                .body(jwtAuthData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Map<String, String> password = new HashMap<>();
        String updatedPassValue = "newPassUpdated";
        password.put("password", updatedPassValue);

        Info updatedPassInfo = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(password)
                .put("api/user")
                .then()
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("User password successfully changed", updatedPassInfo.getMessage());

        jwtAuthData.setPassword(updatedPassValue);

        token = given()
                .contentType(ContentType.JSON)
                .body(jwtAuthData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        FullUser updatedUser = given()
                .auth().oauth2(token)
                .get("/api/user")
                .then()
                .statusCode(200)
                .extract().as(FullUser.class);

        Assertions.assertNotEquals(user.getPass(), updatedUser.getPass());
    }

    // Проверка на невозможность изменить пароль админу
    @Test
    public void negativeChangeAdminPasswordTest(){
        JwtAuthData jwtAuthData = new JwtAuthData("admin", "admin");

        String token = given()
                .contentType(ContentType.JSON)
                .body(jwtAuthData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Map<String, String> password = new HashMap<>();
        String updatedPassValue = "newPassUpdated";
        password.put("password", updatedPassValue);

        Info updatedPassInfo = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(password)
                .put("api/user")
                .then()
                .statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("Cant update base users", updatedPassInfo.getMessage());
    }

    // Проверка на невозможность удаления админа
    @Test
    public void negativeDeleteAdminTest(){
        JwtAuthData jwtAuthData = new JwtAuthData("admin", "admin");

        String token = given()
                .contentType(ContentType.JSON)
                .body(jwtAuthData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Info info = given()
                .auth().oauth2(token)
                .delete("/api/user").then()
                .statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("Cant delete base users", info.getMessage());
    }


    // Проверка на удаление пользака
    @Test
    public void positiveDeleteNewUserTest(){
        int randomNumber = Math.abs(random.nextInt());
        FullUser user = FullUser.builder()
                .login("threadQaTestUser!!! " + randomNumber)
                .pass("123456")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()
                .statusCode(201);

        JwtAuthData jwtAuthData = new JwtAuthData(user.getLogin(), user.getPass());

        String token = given()
                .contentType(ContentType.JSON)
                .body(jwtAuthData)
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Info infoDelete = given()
                .auth().oauth2(token)
                .delete("/api/user").then()
                .statusCode(200)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("User successfully deleted", infoDelete.getMessage());
    }

    // Проверка на получение всех пользаков. Там есть 3 системных пользака, которых никак нельзя удалить. Поэтому проверяем,
    // что список пользаков >= 3
    @Test
    public void positiveGetAllUsersTest(){
        List<String> users = given()
                .get("/api/users")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<List<String>>() {
                });

        Assertions.assertTrue(users.size()>=3);
    }
}
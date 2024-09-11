package service;

import assertions.AssertableResponse;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import models.FullUser;
import models.JwtAuthData;

import java.util.HashMap;


// Спецификация не захордкожена, благодаря AbstractBaseService
public class UserServiceTanya extends AbstractBaseService<UserServiceTanya> {

    protected UserServiceTanya(RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    public AssertableResponse register(FullUser user) {
        return new AssertableResponse(getRequestSpecification()
                .contentType(ContentType.JSON)
                .body(user)
                .post(getApi() + "/signup")
                .then());
    }

    public AssertableResponse auth(FullUser user) {
        JwtAuthData jwtAuthData = new JwtAuthData(user.getLogin(), user.getPass());
        return new AssertableResponse(getRequestSpecification()
                .contentType(ContentType.JSON)
                .body(jwtAuthData)
                .post(getApi() + "/login")
                .then());
    }

    public AssertableResponse getUserInfo(String token) {
        return new AssertableResponse(getRequestSpecification()
                .auth().oauth2(token)
                .get(getApi() + "/user")
                .then());
    }

    public AssertableResponse getUserInfo() {
        return new AssertableResponse(getRequestSpecification()
                .get(getApi() + "/user")
                .then());
    }

    public AssertableResponse updatePass(String token, String newPassword) {
        HashMap<String, String> password = new HashMap<>();
        password.put("password", newPassword);

        return new AssertableResponse(getRequestSpecification()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(password)
                .put(getApi() + "/user")
                .then());
    }
}


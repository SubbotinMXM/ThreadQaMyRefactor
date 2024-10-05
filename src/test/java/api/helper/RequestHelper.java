package api.helper;

import api.utils.AppConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;

import static io.restassured.RestAssured.given;

public class RequestHelper {

    private static final AppConfig CONFIG = ConfigFactory.newInstance().create(AppConfig.class, System.getProperties());

    public static RequestSpecification getDefaultRequestSpec() {
        return given()
                .filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured())
                .log().all()
                .baseUri(CONFIG.baseUrl());
    }
}

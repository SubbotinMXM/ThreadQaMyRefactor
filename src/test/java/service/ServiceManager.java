package service;

import io.restassured.specification.RequestSpecification;

import static helper.RequestHelper.getDefaultRequestSpec;
import static helper.RequestHelper.getMockRequestSpec;

public class ServiceManager {

    // Не очень хороший сервис, пояснения внутри
    public static UserService getUserService(){
        return new UserService();
    }

    // Хороший сервис, пояснения внутри
    public static UserServiceTanya getUserServiceTanya(){
        return new UserServiceTanya(getDefaultRequestSpec());
    }

    // Так сделано на ППРБ на работе
    public static UserServiceTanya getUserServiceTanya2(final RequestSpecification specification){
        return new UserServiceTanya(specification);
    }
}

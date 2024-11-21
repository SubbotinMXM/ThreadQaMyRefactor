package api.service;

import io.restassured.specification.RequestSpecification;

import static api.helper.RequestHelper.getDefaultRequestSpec;

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

    // Очень важная строчка
    // Очень важная строчка 2
    // Очень важная строчка 3
}

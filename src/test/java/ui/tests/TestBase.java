package ui.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

public class TestBase {
    @BeforeAll
    public static void setup() {
        SelenideLogger.addListener("allure", new AllureSelenide());

        boolean isJenkins = Boolean.getBoolean("jenkins");
        if (isJenkins) {
            // Запуск тестов удаленно (./gradlew clean test -Djenkins=true)
            System.out.println("Running tests remotely");
            Configuration.remote = "https://user1:1234@selenoid.autotests.cloud/wd/hub";
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("selenoid:options", Map.of(
                    "enableVNC", true,
                    "enableVideo", true
            ));

            Map <String, Boolean> map = new HashMap();
            map.put("enableVNC", true);
            map.put("enableVideo", true);

            capabilities.setCapability("selenoid:options", map);

            Configuration.browserCapabilities = capabilities;
        } else {
            // Запуск тестов локально (./gradlew clean test)
            System.out.println("Running tests locally");
            System.setProperty("webdriver.chrome.driver", "/Users/maksimsubbotin/Documents/Programm/chromedriver");
        }
    }
}

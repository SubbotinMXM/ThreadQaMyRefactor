package ui.tests;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v110.network.Network;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

@DisplayName("Тесты на селениде")
public class DevtoolsTest extends TestBase {

    private DevTools devTools;
    private final String URL = "https://www.simbirsoft.com/contacts/";

    @Test
    @Description("Проверяем работу devtools с request")
    void checkRequestHeaderByDevtools() {
        open(URL);

        // Инициализируем девтулз и начинаем собирать в мапу все request headers, которые будут на странице URL
        initDevTools();
        Map<String, String> headersByDevTools = getRequestHeadersByDevTools(URL);

        $x("//span[contains(text(),'О нас')]").hover();
        $x("//*[@href='/contacts/']").click();

        Assertions.assertTrue(headersByDevTools.containsKey("https://www.simbirsoft.com/contacts/"));
    }

    @Test
    @Description("Проверяем работу devtools с response")
    void checkResponseHeaderByDevtools() {
        open(URL);

        // Инициализируем девтулз и начинаем собирать в мапу все response headers
        initDevTools();
        Map<String, String> headersByDevTools = getResponseHeadersByDevTools();

        $x("//span[contains(text(),'О нас')]").hover();
        $x("//*[@href='/contacts/']").click();

        Assertions.assertTrue(headersByDevTools.containsKey("https://www.simbirsoft.com/contacts/"));
    }

    public void initDevTools() {
        ChromeDriver driver = (ChromeDriver) WebDriverRunner.getWebDriver();
        devTools = driver.getDevTools();
        devTools.createSession();
    }

    public Map<String, String> getRequestHeadersByDevTools(String url) {
        Map requests = new HashMap<String, String>();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        devTools.addListener(
                Network.requestWillBeSent(),
                entry -> {
                    if (entry.getRequest().getUrl().equals(url)) {
                        requests.put(entry.getRequest().getUrl(), entry.getRequest().getHeaders().toJson());
                    }
                }
        );
        return requests;
    }

    public Map<String, String> getResponseHeadersByDevTools() {
        Map responses = new HashMap<String, String>();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        devTools.addListener(
                Network.responseReceived(),
                entry -> {
                    responses.put(entry.getResponse().getUrl(), entry.getResponse().getHeaders().toJson());
                }
        );
        return responses;
    }
}

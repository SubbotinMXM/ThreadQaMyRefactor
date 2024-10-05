package ui.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;

@DisplayName("Тесты на селениде")
public class SelenideLordfilmTest extends TestBase {

    private final static String word = "дюна";

    @Test
    @DisplayName("Первый тест")
    @Description("Проверяем, что получается на лордфильме найти нужное слово")
    @Owner("Максим Субботин")
    @Tag("smoke")
    void foo1() {
        step("Открываем lordfilm", () -> {
            open("https://mix.lordfilmx.lol/");
        });
        step("Ищем фильм " + word, () -> {
            $x("//input[@placeholder='Введите название']")
                    .setValue("дюна")
                    .pressEnter();
        });
        step("Проверяем, что на странице поиска есть слово " + word, () -> {
            $x("//input[@value='дюна']")
                    .shouldBe(visible);
        });
    }

    @Test
    @DisplayName("Второй тест")
    @Description("Проверяем, что получается на лордфильме найти нужное слово")
    @Owner("Максим Субботин")
    @Tag("smoke")
    void foo2() {
        step("Открываем lordfilm", () -> {
            open("https://mix.lordfilmx.lol/");
        });
        step("Ищем фильм " + word, () -> {
            $x("//input[@placeholder='Введите название']")
                    .setValue("дюна")
                    .pressEnter();
        });
        step("Проверяем, что на странице поиска есть слово " + word, () -> {
            $x("//input[@value='дюна']")
                    .shouldBe(visible);
        });
    }

    @Test
    @DisplayName("Третий тест (сломан)")
    @Description("Проверяем, что получается на лордфильме найти нужное слово")
    @Owner("Максим Субботин")
    @Tag("prod")
    void foo3() {
        step("Открываем lordfilm", () -> {
            open("https://mix.lordfilmx.lol/");
        });
        step("Ищем фильм " + word, () -> {
            $x("//input[@placeholder='Введите название']")
                    .setValue("дюна")
                    .pressEnter();
        });
        step("Проверяем, что на странице поиска есть слово " + word, () -> {
            $x("//input[@value='дывуоав']")
                    .shouldBe(visible);
        });
    }

    @Test
    @DisplayName("Четвертый тест")
    @Description("Проверяем, что получается на лордфильме найти нужное слово")
    @Owner("Максим Субботин")
    @Tag("prod")
    void foo4() {
        step("Открываем lordfilm", () -> {
            open("https://mix.lordfilmx.lol/");
        });
        step("Ищем фильм " + word, () -> {
            $x("//input[@placeholder='Введите название']")
                    .setValue("дюна")
                    .pressEnter();
        });
        step("Проверяем, что на странице поиска есть слово " + word, () -> {
            $x("//input[@value='дюна']")
                    .shouldBe(visible);
        });
    }

    @Test
    @DisplayName("Пятый тест (сломан)")
    @Description("Проверяем, что получается на лордфильме найти нужное слово")
    @Owner("Максим Субботин")
    @Tag("web")
    void foo5() {
        step("Открываем lordfilm", () -> {
            open("https://mix.lordfilmx.lol/");
        });
        step("Ищем фильм " + word, () -> {
            $x("//input[@placeholder='Введите название']")
                    .setValue("дюна")
                    .pressEnter();
        });
        step("Проверяем, что на странице поиска есть слово " + word, () -> {
            $x("//input[@value='кадлвоыаимдв']")
                    .shouldBe(visible);
        });
    }
}

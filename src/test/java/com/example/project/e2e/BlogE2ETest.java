package com.example.project.e2e;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.example.project.pages.BlogPage;
import com.example.project.pages.LoginPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * E2E UI tests using Selenide and Page Object pattern.
 */
public class BlogE2ETest {

  @BeforeAll
  static void setup() {
    // Налаштування браузера
    Configuration.browser = "chrome";
    // Якщо хочеш бачити, як браузер літає, зміни на false:
    Configuration.headless = true;

    // Збільшуємо таймаут, щоб дати серверу час на відповідь (за замовчуванням 4 секунди)
    Configuration.timeout = 8000;
  }

  @Test
  public void userCanLoginAndCreatePost() {
    LoginPage loginPage = new LoginPage();
    BlogPage blogPage = new BlogPage();

    // 1. Відкриваємо сторінку логіну і робимо скріншот
    loginPage.openPage();
    Selenide.screenshot("step1_open_login_page");

    // 2. Авторизуємося
    loginPage.login("Fox", "09125689");

    // ВАЖЛИВО: Чекаємо, поки JavaScript перекине нас на головну сторінку
    webdriver().shouldHave(urlContaining("index.html"));
    Selenide.screenshot("step2_after_login");

    // 3. Створюємо пост і робимо скріншот
    blogPage.createNewPost("Тестовий пост створений через WebDriver!");

    // Робимо невеличку паузу, щоб JavaScript встиг відмалювати новий пост після його відправки
    Selenide.sleep(1000);
    Selenide.screenshot("step3_after_post_creation");

    // 4. Перевіряємо, що пост з'явився (перевіряємо першу картку поста на сторінці)
    blogPage.verifyFirstPostContains("Тестовий пост створений через WebDriver!");
  }
}
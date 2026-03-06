package com.example.project.bdd;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

import com.codeborne.selenide.Configuration;
import com.example.project.pages.BlogPage;
import com.example.project.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BlogSteps {

  private LoginPage loginPage = new LoginPage();
  private BlogPage blogPage = new BlogPage();

  public BlogSteps() {
    Configuration.browser = "chrome";
    Configuration.headless = true; // Запуск у фоні
    Configuration.timeout = 8000;
  }

  @Given("the user opens the login page")
  public void openLoginPage() {
    loginPage.openPage();
  }

  @When("the user enters username {string} and password {string}")
  public void login(String username, String password) {
    loginPage.login(username, password);
  }

  @Then("the user is successfully redirected to the main page")
  public void verifyRedirection() {
    webdriver().shouldHave(urlContaining("index.html"));
  }

  @When("the user creates a new post with text {string}")
  public void createPost(String content) {
    blogPage.createNewPost(content);
    // Додаємо невелику паузу, щоб UI встиг оновитися після створення поста
    com.codeborne.selenide.Selenide.sleep(1000);
  }

  @Then("a post with text {string} should appear in the feed")
  public void verifyPostCreation(String content) {
    blogPage.verifyFirstPostContains(content);
  }
}
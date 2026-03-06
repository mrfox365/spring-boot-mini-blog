package com.example.project.pages;

import static com.codeborne.selenide.Selenide.*;
import com.codeborne.selenide.SelenideElement;

/**
 * Page Object для сторінки авторизації.
 */
public class LoginPage {

  private final SelenideElement usernameInput = $("#loginUsername");
  private final SelenideElement passwordInput = $("#loginPassword");
  private final SelenideElement loginButton = $("#loginForm button[onclick='login()']");

  public LoginPage openPage() {
    open("http://localhost:8080/auth.html");
    return this;
  }

  public void login(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    loginButton.click();
  }
}
package com.example.project.pages;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import com.codeborne.selenide.SelenideElement;

/**
 * Page Object для головної сторінки блогу.
 */
public class BlogPage {

  private final SelenideElement postInput = $("#newPostContent");
  private final SelenideElement submitPostBtn = $(".create-post-box .btn");
  private final SelenideElement firstPostContent = $(".post-card:nth-child(1) .post-content");

  public void createNewPost(String content) {
    postInput.setValue(content);
    submitPostBtn.click();
  }

  public void verifyFirstPostContains(String expectedText) {
    firstPostContent.shouldHave(text(expectedText));
  }
}
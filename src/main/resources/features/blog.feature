Feature: Blog Management
  As an authorized user
  I want to be able to create posts
  So that I can share information with others

  Scenario: Successful creation of a new post
    Given the user opens the login page
    When the user enters username "Fox" and password "09125689"
    Then the user is successfully redirected to the main page
    When the user creates a new post with text "Test post via Cucumber!"
    Then a post with text "Test post via Cucumber!" should appear in the feed
*** Settings ***
Library           SeleniumLibrary
Library           String
Suite Teardown    Close Browser

*** Variables ***
${BASE_URL}          http://localhost:8080/auth.html
${BROWSER}           chrome
${LOGIN_INPUT}       id=loginUsername
${PASSWORD_INPUT}    id=loginPassword
${LOGIN_BTN}         css=#loginForm > button
${POST_TEXTAREA}     id=newPostContent
${SUBMIT_POST_BTN}   css=.create-post-box .btn

*** Test Cases ***
Complex User Scenario With Posts Comments And Profile
    [Documentation]    Комплексний тест: створення постів, коментарів, видалення та редагування профілю.
    Open Browser To Login Page
    Login As Valid User    Fox    09125689
    Wait Until Page Contains    Що у вас нового?

    # 1. Створити пустий пост (Пост 1)
    Create New Post    Пост 1 (без коментарів)
    Sleep    1s

    # 2. Створити пост з коментарем (Пост 2)
    Create New Post    Пост 2 (з коментарем)
    Sleep    1s
    Add Comment To Nth Post    1    Це коментар до Поста 2
    Sleep    1s

    # 3. Створити ще один пост з коментарем (Пост 3)
    Create New Post    Пост 3 (з коментарем)
    Sleep    1s
    Add Comment To Nth Post    1    Це коментар до Поста 3
    Sleep    1s

    # 4. ВИДАЛЕННЯ: Перший створений пост зараз знаходиться внизу (він третій за рахунком на сторінці)
    Delete Nth Post    3
    Sleep    1s

    # 5. ВИДАЛЕННЯ: Пост 2 тепер став другим за рахунком. Спочатку видаляємо його комент, потім сам пост
    Delete Comment In Nth Post    2
    Sleep    1s
    Delete Nth Post    2
    Sleep    1s

    # 6. ВИДАЛЕННЯ: Пост 3 тепер став першим за рахунком. Видаляємо його одразу (комент видалиться каскадно)
    Delete Nth Post    1
    Sleep    1s

    # 7. ПРОФІЛЬ: Відкрити, написати рандомний текст, зберегти
    Open Profile Settings
    ${random_bio}=    Generate Random String    15    [LETTERS]
    Update Bio    Це мій рандомний опис: ${random_bio}
    Sleep    1s

    # 8. ФІНАЛ: Створити пустий пост, клікнути на профіль, закрити і видалити пост
    Create New Post    Фінальний тестовий пост
    Sleep    1s
    View Author Profile From Nth Post    1
    Sleep    1s
    Close Author Profile Modal
    Sleep    1s
    Delete Nth Post    1
    Sleep    1s


*** Keywords ***
Open Browser To Login Page
    Open Browser    ${BASE_URL}    ${BROWSER}
    Maximize Browser Window

Login As Valid User
    [Arguments]    ${username}    ${password}
    Input Text     ${LOGIN_INPUT}       ${username}
    Input Text     ${PASSWORD_INPUT}    ${password}
    Click Button   ${LOGIN_BTN}

Create New Post
    [Arguments]    ${content}
    Input Text       ${POST_TEXTAREA}     ${content}
    Click Element    ${SUBMIT_POST_BTN}

Add Comment To Nth Post
    [Arguments]    ${post_index}    ${comment_text}
    Input Text       css=.post-card:nth-child(${post_index}) .comment-input-box input    ${comment_text}
    Click Element    css=.post-card:nth-child(${post_index}) .comment-input-box button

Delete Nth Post
    [Arguments]    ${post_index}
    Click Element    css=.post-card:nth-child(${post_index}) .post-header button
    Handle Alert     action=ACCEPT

Delete Comment In Nth Post
    [Arguments]    ${post_index}
    Click Element    css=.post-card:nth-child(${post_index}) .comment button
    Handle Alert     action=ACCEPT

Open Profile Settings
    Click Element    id=headerAvatar
    Wait Until Element Is Visible    id=profileMenu
    Click Element    xpath=//div[contains(text(), 'Налаштування профілю')]
    Wait Until Element Is Visible    id=settingsModal

Update Bio
    [Arguments]    ${bio_text}
    Input Text       id=editBio    ${bio_text}
    Click Element    xpath=//button[contains(text(), 'Зберегти зміни')]
    Handle Alert     action=ACCEPT

View Author Profile From Nth Post
    [Arguments]    ${post_index}
    Click Element    css=.post-card:nth-child(${post_index}) .post-author
    Wait Until Element Is Visible    id=viewProfileModal

Close Author Profile Modal
    Click Element    css=#viewProfileModal .close-btn
    Wait Until Element Is Not Visible    id=viewProfileModal
package com.devresume.application.views.login;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.router.Route;

@Route("login-rich-content")
public class LoginRichContent extends Div {

    public LoginRichContent() {
        // See login-rich-content.css
        addClassName("login-rich-content");

        LoginForm loginForm = new LoginForm();
        loginForm.getElement().getThemeList().add("light");
        add(loginForm);
        // Prevent the example from stealing focus when browsing the documentation
        loginForm.getElement().setAttribute("no-autofocus", "");
    }
}
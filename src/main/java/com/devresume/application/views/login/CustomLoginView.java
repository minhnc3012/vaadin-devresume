package com.devresume.application.views.login;

import com.devresume.application.security.AuthenticatedUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "custom-login")
public class CustomLoginView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    LoginI18n i18n = LoginI18n.createDefault();
    com.vaadin.flow.component.login.LoginForm loginForm = new com.vaadin.flow.component.login.LoginForm();
    public CustomLoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("DevResume");
        i18n.getHeader().setDescription("Login using user/user or admin/admin");
        i18n.setAdditionalInformation(null);

        
        loginForm.setI18n(i18n);
        loginForm.setForgotPasswordButtonVisible(true);
        
        loginForm.setAction("custom-login");
        
        loginForm.addForgotPasswordListener(event -> {
            Notification.show("ForgotPasswordListener");
            UI.getCurrent().navigate(ForgotPasswordView.class);
        });

        Button googleLoginButton = new Button("Login with Google", new Icon("vaadin", "google-plus"));
        googleLoginButton.addClickListener(event -> {
            UI.getCurrent().getPage().setLocation("/oauth2/authorization/google");
        });

        Button createAccountButton = new Button("Create Account");
        createAccountButton.addClickListener(event -> {
            Notification.show("Create Account button clicked");
            // Thực hiện xử lý tạo tài khoản ở đây
            UI.getCurrent().navigate(CreateAccountView.class);
        });

        // Tạo layout chứa nút "Forgot Password" và "Create Account"
        VerticalLayout buttonsLayout = new VerticalLayout(
                loginForm, googleLoginButton, createAccountButton);
        buttonsLayout.setSpacing(true);
        buttonsLayout.setAlignItems(Alignment.CENTER);
        // Thêm layout vào trang đăng nhập
        add(buttonsLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Đã đăng nhập
            event.forwardTo("");
        }

        loginForm.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}

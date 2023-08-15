package com.devresume.application.views.login;

import com.devresume.application.security.AuthenticatedUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("DevResume");
        i18n.getHeader().setDescription("Login using user/user or admin/admin");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(true);
        
        addForgotPasswordListener(event -> {
            // this.close();
            Notification.show("ForgotPasswordListener");
            UI.getCurrent().navigate(ForgotPasswordView.class);
      });
        
        setOpened(true);

        // addRememberMeCheckbox();
        addGoogleLoginButton();
    }

    
    public void addRememberMeCheckbox() {
        Checkbox rememberMe = new Checkbox("Remember me");
        rememberMe.getElement().setAttribute("name", "remember-me").setAttribute("id", "rememberMeCheckbox");
        Element loginFormElement = getElement();
        Element element = rememberMe.getElement();
        loginFormElement.appendChild(element);

        String executeJsForFieldString = "const field = document.getElementById($0);" +
                "if(field) {" +
                "   field.after($1)" +
                "} else {" +
                "   console.error('could not find field', $0);" +
                "}";
        getElement().executeJs(executeJsForFieldString, "vaadinLoginPassword", element);
    }
    
    public void addGoogleLoginButton() {
    	 Button googleLoginButton = new Button("Login with Google", new Icon("vaadin", "google-plus"));
         googleLoginButton.addClickListener(event -> {
             UI.getCurrent().getPage().setLocation("/oauth2/authorization/google");
         });
         
        googleLoginButton.getElement()
        	.setAttribute("style", "margin-top: var(--lumo-space-m); color: #ea4335")
        	.setAttribute("type", "button")
        	.setAttribute("name", "google-login")
        	.setAttribute("id", "googleLoginButton");
        Element loginFormElement = getElement();
        Element element = googleLoginButton.getElement();
        loginFormElement.appendChild(element);

        String executeJsForFieldString = """
			const field = document.getElementById($0);
			if(field) { 
				field.after($1)
			}
        """;
        getElement().executeJs(executeJsForFieldString, "vaadinLoginPassword", element);
    }
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
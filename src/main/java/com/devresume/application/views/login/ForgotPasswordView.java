package com.devresume.application.views.login;

import com.devresume.application.security.AuthenticatedUser;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Forgot Password")
@Route(value = "forgot-password")
public class ForgotPasswordView extends VerticalLayout {

    public ForgotPasswordView(AuthenticatedUser authenticatedUser) {
      
    }    
}

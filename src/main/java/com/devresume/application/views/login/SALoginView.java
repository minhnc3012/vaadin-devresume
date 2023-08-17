package com.devresume.application.views.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.devresume.application.security.AuthenticatedUser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "salogin")
public class SALoginView extends VerticalLayout implements BeforeEnterObserver{
	
	@Autowired
	private AuthenticatedUser authenticatedUser;
	
	private LoginOverlay login = new LoginOverlay();
	
    public SALoginView() {
        login.setOpened(true);
        login.setTitle("DevResume");
        login.setVisible(false);
        login.setAction("login");
        Button button = new Button("Register");

        add(button);
    }
	
	@Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
        	login.setOpened(false);
            event.forwardTo("");
        }

        login.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}

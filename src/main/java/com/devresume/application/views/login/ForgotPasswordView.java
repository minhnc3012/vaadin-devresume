package com.devresume.application.views.login;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.devresume.application.entity.User;
import com.devresume.application.security.AuthenticatedUser;
import com.devresume.application.service.MailService;
import com.devresume.application.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AnonymousAllowed
@PageTitle("Forgot Password")
@Route(value = "forgot-password")
public class ForgotPasswordView extends Div implements BeforeEnterObserver {
    private static final long serialVersionUID = 1L;
    private final Logger log = LoggerFactory.getLogger(ForgotPasswordView.class);
    
    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;
    
    private TextField email = new TextField("Email");

    private Button cancel = new Button("Cancel");
    private Button send = new Button("Send");

    private ForgotPasswordForm forgotPasswordForm;

    private BeanValidationBinder<ForgotPasswordForm> binder = new BeanValidationBinder<>(ForgotPasswordForm.class);

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.forgotPasswordForm = new ForgotPasswordForm();
        binder.readBean(this.forgotPasswordForm);
    }

    public ForgotPasswordView(AuthenticatedUser authenticatedUser) {
        addClassName("forgot-password-view");
        add(new H3("Forgot Password"));
        add(createFormLayout());
        add(createButtonLayout());

        binder.bindInstanceFields(this);

        send.addClickListener(e -> {
            try {
                binder.writeBean(this.forgotPasswordForm);
                Optional<User> user = userService.requestPasswordReset(forgotPasswordForm.getEmail());
                if(user.isPresent()) {
                    mailService.sendPasswordResetMail(user.get());
                } else {
                    log.warn("Password reset requested for non existing mail");
                    Notification n = Notification.show("Password reset requested for non existing mail.");
                    n.setPosition(Position.BOTTOM_CENTER);
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                UI.getCurrent().navigate(CustomLoginView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.BOTTOM_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification n = Notification.show(
                        "Failed to update the data. Check again that all values are valid");
                n.setPosition(Position.BOTTOM_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        cancel.addClickListener(e -> {
            UI.getCurrent().navigate(CustomLoginView.class);
        });
    }

    private Component createFormLayout() {
        addClassName("forgot-password-view");
        FormLayout formLayout = new FormLayout();
        formLayout.add(email, 2);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        send.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(cancel, send);
        return buttonLayout;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class ForgotPasswordForm {
        @NotEmpty()
        @Email
        private String email;
    }
}

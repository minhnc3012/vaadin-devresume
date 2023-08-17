package com.devresume.application.views.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.devresume.application.entity.User;
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

@AnonymousAllowed
@PageTitle("Create Account")
@Route(value = "create-account")
public class CreateAccountView extends Div implements BeforeEnterObserver {

    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;
    
    private TextField username = new TextField("Email");
    private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last Name");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private User user;
    private BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

    public CreateAccountView() {
        addClassName("create-account-view");
        add(new H3("Create Account"));
        add(createFormLayout());

        add(createButtonLayout());

        binder.bindInstanceFields(this);

        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            try {
                if (this.user == null) {
                    this.user = new User();
                }
                binder.writeBean(this.user);
                User user = userService.registerUser(this.user);
                // mailService.sendActivationEmail(user);

                clearForm();
                Notification.show("Data updated");
                UI.getCurrent().navigate(CustomLoginView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(username, 2);
        formLayout.add(firstName, lastName);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(cancel, save);
        return buttonLayout;
    }

    private void clearForm() {
        binder.readBean(null);
    }

    private void populateForm(User value) {
        this.user = value;
        binder.readBean(this.user);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        populateForm(new User());
    }
}

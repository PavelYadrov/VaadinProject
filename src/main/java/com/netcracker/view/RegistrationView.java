package com.netcracker.view;


import com.netcracker.dto.UserRegisterDTO;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.router.Route;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;

@Route("register")
@CssImport("./styles/register-page.css")
public class RegistrationView extends VerticalLayout {
    @Autowired
    private UserService userService;

    @Autowired
    private FeignUserService feignUserService;

    private PasswordField passwordField1;
    private PasswordField passwordField2;

    private BeanValidationBinder<UserRegisterDTO> binder;

    private boolean enablePasswordValidation;

    public RegistrationView() {

        this.userService=userService;

        Label title = new Label("Sign up form");
        title.addClassName("label");

        TextField firstnameField = new TextField("First name");
        TextField lastnameField = new TextField("Last name");
        TextField username = new TextField("Username");
        EmailField emailField = new EmailField("Email");
        passwordField1 = new PasswordField("Wanted password");
        passwordField2 = new PasswordField("Password again");
        Span errorMessage = new Span();
        Button submitButton = new Button("Sing up");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        FormLayout formLayout = new FormLayout(title, username, firstnameField, lastnameField, passwordField1, passwordField2,
                emailField, errorMessage, submitButton);

        formLayout.setMaxWidth("500px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.getStyle().set("margin-top", "200px");
        formLayout.setMaxHeight("800px");

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("490px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        formLayout.setColspan(title, 2);
        formLayout.setColspan(username, 2);
        formLayout.setColspan(errorMessage, 2);
        formLayout.setColspan(submitButton, 2);

        errorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
        errorMessage.getStyle().set("padding", "15px 0");
        errorMessage.getStyle().set("text-align","center");
        formLayout.addClassName("register-form");

        add(formLayout);

        binder = new BeanValidationBinder<UserRegisterDTO>(UserRegisterDTO.class);

        binder.forField(firstnameField).withValidator(this::validateName).asRequired().bind("firstName");

        binder.forField(lastnameField).withValidator(this::validateName).asRequired().bind("lastName");

        binder.forField(username).withValidator(this::validateUsername).asRequired().bind("username");

        binder.forField(emailField).asRequired("Email is not valid").bind("email");

        binder.forField(passwordField1).asRequired().withValidator(this::passwordValidator).bind("password");

        passwordField2.addValueChangeListener(e -> {

            enablePasswordValidation = true;

            binder.validate();
        });

        binder.setStatusLabel(errorMessage);

        submitButton.addClickListener(e -> {
            try {

                UserRegisterDTO newUser = new UserRegisterDTO();

                binder.writeBean(newUser);

                feignUserService.registration(newUser);
                showSuccess(newUser);
                UI.getCurrent().getPage().setLocation("login");

            }catch ( ValidationException e1){
                errorMessage.setText("Please enter valid information");
            }
            catch (FeignException.BadRequest e2) {
                errorMessage.setText("Username or email address already registered");
            }
        });

    }
    private void showSuccess(UserRegisterDTO detailsBean) {
        Notification notification = Notification.show("Data saved, welcome " + detailsBean.getUsername());
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private ValidationResult passwordValidator(String pass1, ValueContext ctx) {
        if (pass1 == null || pass1.length() < 6) {
            return ValidationResult.error("Password should be at least 6 characters long");
        }
        if (!enablePasswordValidation) {
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }
        String pass2 = passwordField2.getValue();

        if (pass1.equals(pass2)) {
            return ValidationResult.ok();
        }
        return ValidationResult.error("Passwords do not match");
    }

    private ValidationResult validateUsername(String username, ValueContext ctx) {

        String errorMsg = userService.validateUsername(username);

        if (errorMsg == null) {
            return ValidationResult.ok();
        }

        return ValidationResult.error(errorMsg);
    }

    private ValidationResult validateName(String name, ValueContext ctx) {

        String msg = userService.isValid(name);

        if (msg==null) {
            return ValidationResult.ok();
        }

        return ValidationResult.error(msg);
    }

}

package com.netcracker.view;

import com.netcracker.dto.LoginForm;
import com.netcracker.service.FeignUserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.Cookie;


@Route("login")
@CssImport("./styles/login-page.css")
public class LoginView extends VerticalLayout {

    @Autowired
    private FeignUserService feignUserService;

    private TextField username;
    private PasswordField passwordField;

    public LoginView(){

        username = new TextField("Username");
        passwordField = new PasswordField("Password");

        Button submitButton = new Button("Sing in");

        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button redirectButton = new Button("Sing up now",event -> UI.getCurrent().navigate("register"));
        redirectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        username.setValueChangeMode(ValueChangeMode.ON_BLUR);
        passwordField.setValueChangeMode(ValueChangeMode.ON_BLUR);

        Span errorMessage = new Span();
        errorMessage.getStyle().set("color", "red");
        errorMessage.getStyle().set("padding", "15px 0");
        errorMessage.getStyle().set("text-align", "center");

        Label label = new Label("Sing in");
        label.setClassName("label");

        Span or = new Span("Dont have account? Sing up now for free!");
        or.addClassName("or");

        FormLayout formLayout = new FormLayout(label,username,passwordField,errorMessage,submitButton,or,redirectButton);

        formLayout.setMaxWidth("500px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.getStyle().set("margin-top", "200px");
        formLayout.setMaxHeight("500px");
        formLayout.addClassName("login-form");

        submitButton.addClickListener(event ->{
            LoginForm loginForm = new LoginForm(username.getValue(),passwordField.getValue());
            try{
                ResponseEntity<String> response = feignUserService.login(loginForm);
                String token = response.getBody();

                Cookie tokenCookie = new Cookie("Authentication", token);
                VaadinService.getCurrentResponse().addCookie(tokenCookie);

                UI.getCurrent().getPage().setLocation("mainPage");
            }
            catch (FeignException.BadRequest e){
                errorMessage.setText("Invalid username or password");
            }
            catch (FeignException.Forbidden e){
                errorMessage.setText("This account was banned. " +
                        "Please contact admin for detail information");
            }
        });

        add(formLayout);
    }
}

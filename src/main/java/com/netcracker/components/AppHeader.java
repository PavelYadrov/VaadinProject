package com.netcracker.components;

import com.netcracker.dto.UserDTO;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinService;

import javax.servlet.http.Cookie;

@CssImport("./styles/app-header.css")
public class AppHeader extends HorizontalLayout {

    public AppHeader(UserService u, UserDTO userDTO){
        addClassName("main-header");
        Button button = new Button("Logout");
        button.addClickListener(event ->{
            Cookie auth = new Cookie("Authentication",null);
            VaadinService.getCurrentResponse().addCookie(auth);
            UI.getCurrent().getPage().setLocation("login");
        });
        setSizeFull();
        button.addClassName("BUTTON-LOGOUT");

       Span username = new Span("Welcome, "+userDTO.getUsername()+"!");
       username.addClassName("span-username");

        add(button,username);
    }
}

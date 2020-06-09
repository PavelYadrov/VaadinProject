package com.netcracker.components;

import com.netcracker.dto.UserDTO;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.VaadinService;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CssImport("./styles/app-header.css")
public class AppHeader extends HorizontalLayout {

    public AppHeader(Boolean mainButton, UserDTO userDTO) {
        addClassName("main-header");
        if (userDTO == null) return;

        Button button = new Button("Logout");
        button.addClickListener(event -> {
            Cookie auth = new Cookie("Authentication", null);
            VaadinService.getCurrentResponse().addCookie(auth);
            UI.getCurrent().getPage().setLocation("login");
        });
        setSizeFull();
        button.addClassName("button-logout");
        button.setSizeFull();

        Div user = new Div();

        Span welcome = new Span("Welcome, ");
        welcome.addClassName("span-username");

        Span username = new Span(userDTO.getUsername() + "!");
        username.addClassName("user-href");
        username.addClickListener(spanClickEvent -> {
            List<String> param1 = new ArrayList<>();
            Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();
            param1.add(userDTO.getId().toString());

            parametersMap.put("id", param1);

            QueryParameters qp = new QueryParameters(parametersMap);
            UI.getCurrent().navigate("user", qp);
        });
        user.addClassName("username");
        user.add(username, welcome);

        username.addClassName("span-username");

        if (mainButton) {
            Button main = new Button("Main Page");
            main.addClickListener(buttonClickEvent -> {
                UI.getCurrent().getPage().setLocation("mainPage");
            });
            main.addClassName("button-main");
            main.setSizeFull();
            add(main);
        }

        add(button, user);
    }
}

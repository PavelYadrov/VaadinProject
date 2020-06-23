package com.netcracker.components;

import com.netcracker.dto.UserDTO;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.VaadinService;
import reactor.core.publisher.Flux;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CssImport("./styles/app-header.css")
public class AppHeader extends HorizontalLayout {

    private Flux<ChatEvent> messagesFlux;

    private Icon icon;

    private FeignUserService feign;

    private String token;

    public AppHeader(Boolean mainButton, Boolean messagesButton, Boolean disabledName, UserDTO userDTO,
                     FeignUserService feignUserService, UserService userService) {

        this.feign = feignUserService;
        this.messagesFlux = userService.getMessages();
        token = userService.getCookieByName("Authentication");

        addClassName("main-header");
        if (userDTO == null) return;
        this.feign = feignUserService;

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
        user.getStyle().set("display", "contents");

        Span welcome = new Span("Welcome ");
        welcome.addClassName("span-username");

        Span username = new Span(userDTO.getFirstName() + " " + userDTO.getLastName() + "!");
        username.addClassName("user-href");
        if (disabledName) {
            username.addClickListener(spanClickEvent -> {
                setParam("user", userDTO.getId().toString());
            });
        } else {
            username.addClickListener(spanClickEvent -> {
                UI.getCurrent().getPage().reload();
            });
        }

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
            add(button, user, main);
        } else {
            add(button, user);
        }
        if (messagesButton) {
            Button messages = new Button("Messages");
            messages.addClickListener(buttonClickEvent -> {
                if (feign.getUserRooms(token).getBody().isEmpty()) {
                    Notification notification = new Notification("You dont receive or send any messages yet");
                    notification.setPosition(Notification.Position.TOP_CENTER);
                    notification.open();
                    notification.setDuration(4000);
                } else {
                    UI.getCurrent().getPage().setLocation("chat");
                }
            });
            messages.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            messages.addClassName("messages-button");
            if (feign.hasUnreadMessages(userService.getCookieByName("Authentication")).getBody()) {
                Icon icon = new Icon(VaadinIcon.CIRCLE);
                icon.setColor("red");
                messages.setIcon(icon);
                messages.setIconAfterText(true);
            }
            messagesFlux.subscribe(message -> {
                if (userDTO.getId().equals(message.getMessage().getReceiverId())) {
                    getUI().ifPresent(ui ->
                            ui.access(() -> {
                                if (icon == null) {
                                    icon = new Icon(VaadinIcon.CIRCLE);
                                    icon.setColor("red");
                                    messages.setIcon(icon);
                                    messages.setIconAfterText(true);
                                }
                            }));
                }
            });
            add(button, messages, user);
        } else {
            add(button, user);
        }

    }

    private void setParam(String page, String param) {
        List<String> param1 = new ArrayList<>();
        Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();
        param1.add(param);

        parametersMap.put("id", param1);

        QueryParameters qp = new QueryParameters(parametersMap);
        UI.getCurrent().navigate(page, qp);
    }
}

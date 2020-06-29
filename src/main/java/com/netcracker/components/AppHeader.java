package com.netcracker.components;

import com.netcracker.dto.UserDTO;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinService;
import reactor.core.publisher.Flux;

import javax.servlet.http.Cookie;

@CssImport("./styles/app-header.css")
public class AppHeader extends HorizontalLayout {

    private VerticalLayout view;

    private FeederThread thread;

    protected Flux<ChatEvent> messagesFlux;

    private UserDTO userDTO;

    private Button messages = new Button();

    private Icon icon;

    private FeignUserService feign;

    private String token;

    public AppHeader(Boolean mainButton, Boolean messagesButton, Boolean disabledName, UserDTO userDTO,
                     FeignUserService feignUserService, UserService userService, VerticalLayout view) {
        this.view = view;
        this.userDTO = userDTO;
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
                userService.setParam("user", userDTO.getId().toString());
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
                UI.getCurrent().navigate("mainPage");
            });
            main.addClassName("button-main");
            main.setSizeFull();
            add(button, user, main);
        } else {
            add(button, user);
        }
        if (messagesButton) {
            messages = new Button("Messages");
            messages.addClickListener(buttonClickEvent -> {
                if (feign.getUserRooms(token).getBody().isEmpty()) {
                    Notification notification = new Notification("You dont receive or send any messages yet");
                    notification.setPosition(Notification.Position.TOP_CENTER);
                    notification.open();
                    notification.setDuration(4000);
                } else {
                    UI.getCurrent().navigate("chat");
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

            add(button, messages, user);
        } else {
            add(button, user);
        }
    }

    private class FeederThread extends Thread {
        private final UI ui;
        private final VerticalLayout view;

        private int count = 0;

        public FeederThread(UI ui, VerticalLayout view) {
            this.ui = ui;
            this.view = view;
        }

        @Override
        public void run() {
            subscribe(ui);
        }

        public void subscribe(UI ui) {
            messagesFlux.subscribe(message -> {
                if (userDTO.getId().equals(message.getMessage().getReceiverId())) {
                    try {
                        ui.access(() -> {
                            if (icon == null) {
                                icon = new Icon(VaadinIcon.CIRCLE);
                                icon.setColor("red");
                                messages.setIcon(icon);
                                messages.setIconAfterText(true);
                            }
                        });
                    } catch (Exception e) {
                        Thread thread = Thread.currentThread();
                        System.out.println(thread.toString());
                        thread.interrupt();
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        thread = new FeederThread(attachEvent.getUI(), view);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        thread.interrupt();
        thread = null;
    }

}

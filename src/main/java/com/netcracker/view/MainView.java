package com.netcracker.view;

import com.netcracker.dto.UserDTO;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;


@Route("mainPage")
public class MainView extends VerticalLayout {

    private UserDTO user;

    private UserService userService;

    private FeignUserService feign;

    public MainView(@Autowired FeignUserService feignUserService,@Autowired UserService userService){

        this.userService=userService;
        this.feign=feignUserService;

        try {
            String token = userService.getCookieByName("Authentication");
            user = feign.getUserInfo(token).getBody();
        }
        catch (FeignException.Forbidden e ){
            UI.getCurrent().getPage().setLocation("login");
        }

        Notification notification = Notification.show("Data saved, welcome " + user);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }

}

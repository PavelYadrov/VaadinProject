package com.netcracker.view;

import com.netcracker.dto.UserDTO;
import com.netcracker.service.CategoryService;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class BlankRouteView extends VerticalLayout {

    private UserDTO user;

    private UserService userService;

    private FeignUserService feign;

    private String token;

    public BlankRouteView(@Autowired FeignUserService feignUserService,
                          @Autowired UserService userService, @Autowired CategoryService categoryService) {
        this.userService = userService;
        this.feign = feignUserService;

        try {
            token = userService.getCookieByName("Authentication");
            user = feign.getUserInfo(token).getBody();
        } catch (FeignException.Forbidden e) {
            UI.getCurrent().getPage().setLocation("login");
        }
        if (user != null) {
            UI.getCurrent().getPage().setLocation("mainPage");
        }
    }
}

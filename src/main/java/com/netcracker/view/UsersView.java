package com.netcracker.view;

import com.netcracker.dto.UserDTO;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import feign.FeignException;

import java.util.List;

@Route("users")
public class UsersView extends VerticalLayout {

    private FeignUserService feign;
    private UserService userService;
    private String token;
    private UserDTO user;
    private TextField search = new TextField();

    private Grid<UserDTO> users = new Grid<UserDTO>(UserDTO.class);

    public UsersView(FeignUserService feignUserService, UserService userService) {
        this.userService = userService;
        this.feign = feignUserService;
        try {
            token = userService.getCookieByName("Authentication");
            user = feign.getUserInfo(token).getBody();
            if (user != null) {
                try {
                    feign.roleCheck(token);
                    configureGrid();
                    add(search, users);
                    updateGrid();
                    configureFilter();

                    users.addItemDoubleClickListener(userDTOItemClickEvent -> {
                        userService.setParam("user", userDTOItemClickEvent.getItem().getId().toString());
                    });
                } catch (FeignException.Forbidden e) {
                    UI.getCurrent().getPage().setLocation("mainPage");
                }
            } else UI.getCurrent().getPage().setLocation("mainPage");
        } catch (FeignException.Forbidden e) {
            UI.getCurrent().getPage().setLocation("mainPage");
        }
    }

    private void configureFilter() {
        search.setPlaceholder("Username filter...");
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        search.addValueChangeListener(e -> updateGrid());
    }

    private void updateGrid() {
        List<UserDTO> userList;
        if (!search.getValue().isEmpty()) {
            userList = feign.getAllUsers(token, search.getValue()).getBody();
        } else {
            userList = feign.getAllUsers(token, "").getBody();
        }
        users.setItems(userList);
    }

    private void configureGrid() {
        users.addClassName("users-grid");
        users.setWidth("100%");
        users.setHeight("900px");
        users.setColumnReorderingAllowed(true);
        users.setColumns("id", "username", "firstName", "lastName", "status", "email", "regDate");
    }
}

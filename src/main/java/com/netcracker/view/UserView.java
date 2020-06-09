package com.netcracker.view;

import com.netcracker.components.AppHeader;
import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.CustomPair;
import com.netcracker.dto.MiniAdvertisement;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.CategoryService;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Route("user")
@CssImport("./styles/user-page.css")
public class UserView extends VerticalLayout implements HasUrlParameter<String> {

    private String token;

    private UserDTO user;

    private UserDTO owner;

    private UserService userService;

    private FeignUserService feign;

    private String param;

    private String imageRoute = "http://localhost:8090/images/";

    private List<AdvertisementDTO> usersAdvertisements;

    private Image avatar = new Image();
    private Span person = new Span();
    private Span email = new Span();
    private Span registrationDate = new Span();
    private VerticalLayout userInfo = new VerticalLayout();
    private VerticalLayout image = new VerticalLayout();
    private VerticalLayout advertisements = new VerticalLayout();
    private Dialog changePasswordWindow = new Dialog();
    private Dialog changeStatusWindow = new Dialog();

    private Button delete = new Button("Delete User");
    private Button changePassword = new Button("Change Password");
    private Button changeStatus = new Button("Change Status");

    private Button submitPassword = new Button("Change Password");
    private Button submitStatus = new Button("Change Status");

    private Registration deleteListener;
    private Registration passListener;
    private Registration submitPassListener;
    private Registration statusListener;
    private Registration submitStatusListener;

    private TextField password = new TextField("Password");
    private TextField status = new TextField("Status");

    private List<String> statusList = new ArrayList<>();


    public UserView(@Autowired FeignUserService feignUserService,
                    @Autowired UserService userService, @Autowired CategoryService categoryService) {

        this.userService = userService;
        this.feign = feignUserService;


        try {
            token = userService.getCookieByName("Authentication");
            user = feign.getUserInfo(token).getBody();
        } catch (FeignException.Forbidden e) {
            UI.getCurrent().getPage().setLocation("login");
        }
        //TODO implement convenient style
        if (user != null) {
            statusList.add("Banned");
            statusList.add("Frozen");
            statusList.add("Active");

            avatar.addClassName("user-avatar");
            userInfo.addClassName("user-info");
            person.addClassName("user");
            email.addClassName("user");
            registrationDate.addClassName("user");

            userInfo.setMinWidth("600px");
            userInfo.setMinHeight("600px");

            delete.getStyle().set("margin-left", "3%");
            delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            delete.getStyle().set("cursor", "pointer");

            changeStatus.getStyle().set("margin-left", "3%");
            changeStatus.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            changeStatus.getStyle().set("cursor", "pointer");

            changePassword.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            changePassword.getStyle().set("margin-left", "3%");
            changePassword.getStyle().set("cursor", "pointer");

            submitStatus.getStyle().set("cursor", "pointer");
            submitStatus.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            submitPassword.getStyle().set("cursor", "pointer");
            submitPassword.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            advertisements.setMaxHeight("600px");
            advertisements.setMinWidth("400px");
            advertisements.setSizeFull();

            add(changePasswordWindow, changeStatusWindow);
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        if (user != null) {
            param = userService.setParam(beforeEvent, s);
            if (param == null) return;

            ResponseEntity<UserDTO> response = feign.getUserById(token, param);

            if (response.getStatusCode() != HttpStatus.OK) {
                UI.getCurrent().getPage().setLocation("mainPage");
                return;
            } else {
                owner = response.getBody();
                usersAdvertisements = feign.getUsersAdvertisements(token, owner.getId().toString()).getBody();
            }

            loadUserPage();
        }
    }

    private void loadUserPage() {
        this.removeAll();
        add(new AppHeader(true, user));

        if (deleteListener != null) deleteListener.remove();
        if (passListener != null) passListener.remove();
        if (statusListener != null) statusListener.remove();
        if (submitPassListener != null) submitPassListener.remove();
        if (submitStatusListener != null) submitStatusListener.remove();

        avatar.setSrc(imageRoute + owner.getAvatar());
        person.setText("Full Name: " + owner.getLastName() + " " + owner.getFirstName());
        registrationDate.setText("Registration date: "
                + owner.getRegDate().toLocaleString().substring(0, owner.getRegDate().toLocaleString().length() - 3));

        userInfo.add(person);
        userInfo.add(registrationDate);
        image.add(avatar);

        try {
            feign.roleCheck(token);
            email.setText("Email: " + owner.getEmail());
            userInfo.add(email);

            Div tools = new Div();
            tools.getStyle().set("width", "100%");
            tools.add(delete, changePassword, changeStatus);
            userInfo.add(tools);
            deleteListener = delete.addClickListener(buttonClickEvent -> {
                feign.deleteUser(token, owner.getId().toString());
                UI.getCurrent().getPage().setLocation("mainPage");
            });

            passListener = changePassword.addClickListener(buttonClickEvent -> {
                changePasswordWindow.add(password, submitPassword);
                changePasswordWindow.open();
            });
            submitPassListener = submitPassword.addClickListener(buttonClickEvent -> {
                CustomPair pair = new CustomPair();
                pair.setFirstLine(owner.getId().toString());
                pair.setSecondLine(password.getValue());
                feign.changeUserPassword(token, pair);
                changePasswordWindow.removeAll();
                changePasswordWindow.close();
            });

            statusListener = changeStatus.addClickListener(buttonClickEvent -> {
                VerticalLayout statuses = new VerticalLayout();
                statusList.forEach(s -> {
                    Span statusAwailable = new Span(s);
                    statusAwailable.addClickListener(spanClickEvent -> {
                        status.setValue(statusAwailable.getText());
                    });
                    statusAwailable.getStyle().set("cursor", "pointer");
                    statuses.add(statusAwailable);
                });
                changeStatusWindow.add(status, submitStatus, statuses);
                changeStatusWindow.open();
            });
            submitStatusListener = submitStatus.addClickListener(buttonClickEvent -> {
                CustomPair pair = new CustomPair();
                pair.setFirstLine(owner.getId().toString());
                pair.setSecondLine(status.getValue());
                feign.changeUserStatus(token, pair);
                changeStatusWindow.removeAll();
                changeStatusWindow.close();
            });
        } catch (FeignException.Forbidden e) {
        }

        if (!usersAdvertisements.isEmpty()) {
            Div advs = new Div();
            advs.setMaxWidth("700px");
            advs.setMaxHeight("450px");
            advs.getStyle().set("overflow", "auto");
            usersAdvertisements.stream()
                    .map(advertisementDTO -> {
                        MiniAdvertisement min = new MiniAdvertisement(advertisementDTO);
                        if (min.getDescription().getText().length() > 20) {
                            min.getDescription().setText(min.getDescription().getText().substring(0, 20) + "...");
                        }
                        return min;
                    })
                    .forEach(miniAdvertisement -> advertisements.add(miniAdvertisement));
            advs.add(advertisements);
            userInfo.add(advs);
        }


        Div content = new Div(image, userInfo);
        content.addClassName("user-page");
        add(content);

    }
}

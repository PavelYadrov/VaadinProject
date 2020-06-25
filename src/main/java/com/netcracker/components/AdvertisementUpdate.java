package com.netcracker.components;

import com.netcracker.dto.AdvertisementAddBinder;
import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;

public class AdvertisementUpdate extends VerticalLayout {

    private Label headAdvertisement = new Label("Update Advertisement Info");
    private TextField name = new TextField("Title");
    private TextField description = new TextField("Description");
    private TextField price = new TextField("Price");

    private TextField category = new TextField("Category Id");
    private TextField owner = new TextField("User Id");
    private TextField date = new TextField("Registration Date");

    private Boolean admin;

    private AdvertisementDTO advertisementDTO;

    private UserService userService;

    private Button submitButton = new Button("Update Advertisement");

    private BeanValidationBinder<AdvertisementAddBinder> binder;

    public AdvertisementUpdate(UserDTO user, FeignUserService feignUserService, Boolean adminFlag,
                               UserService userService, Dialog dialog, AdvertisementDTO advertisementDTO) {
        addClassName("advertisement-add");
        this.userService = userService;
        this.advertisementDTO = advertisementDTO;
        this.admin = adminFlag;
        binder = new BeanValidationBinder<>(AdvertisementAddBinder.class);
        headAdvertisement.addClassName("adv-head");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setMaxWidth("800");
        submitButton.setSizeFull();

        name.setMaxWidth("800");
        name.setValue(advertisementDTO.getName());
        name.setSizeFull();

        description.setMaxWidth("800");
        description.setValue(advertisementDTO.getDescription());
        description.setSizeFull();

        price.setMaxWidth("800");
        price.setValue(advertisementDTO.getPrice().toString());
        price.setSizeFull();

        if (admin) {
            owner.setMaxWidth("800");
            owner.setValue(advertisementDTO.getUser_id().toString());
            owner.setSizeFull();

            category.setMaxWidth("800");
            category.setValue(advertisementDTO.getCategory_id().toString());
            category.setSizeFull();
        }

        submitButton.addClickListener(buttonClickEvent -> {
            String token = userService.getCookieByName("Authentication");
            advertisementDTO.setName(name.getValue());
            advertisementDTO.setDescription(description.getValue());
            advertisementDTO.setPrice(Double.parseDouble(price.getValue()));
            if (admin) {
                advertisementDTO.setUser_id(advertisementDTO.getUser_id());
                advertisementDTO.setCategory_id(Long.parseLong(category.getValue()));
                feignUserService.adminUpdateAdvertisement(token, advertisementDTO);
                dialog.close();
                dialog.removeAll();
                UI.getCurrent().getPage().reload();
                return;
            }
            feignUserService.updateAdvertisement(token, advertisementDTO);
            dialog.close();
            dialog.removeAll();
            UI.getCurrent().getPage().reload();
        });
        Div wrapper = new Div();
        wrapper.add(headAdvertisement);

        VerticalLayout head = new VerticalLayout(name, description, price);
        if (adminFlag) {
            head.add(category);
        }
        head.setMaxWidth("800");
        head.setSizeFull();
        wrapper.add(head, submitButton);
        wrapper.setMaxWidth("970");
        wrapper.setSizeFull();
        wrapper.addClassName("adv-add-wrapper");

        add(wrapper);

        binder.forField(name).withValidator(this::validateText).asRequired().bind("name");
        binder.forField(description).withValidator(this::validateText).asRequired().bind("description");
        binder.forField(price).withValidator(this::validatePrice).asRequired().bind("price");

    }

    private ValidationResult validateText(String text, ValueContext ctx) {

        String errorMsg = userService.validateText(text);

        if (errorMsg == null) {
            return ValidationResult.ok();
        }

        return ValidationResult.error(errorMsg);
    }

    private ValidationResult validatePrice(String price, ValueContext ctx) {

        String errorMsg = userService.validatePrice(price);

        if (errorMsg == null) {
            return ValidationResult.ok();
        }

        return ValidationResult.error(errorMsg);
    }
}


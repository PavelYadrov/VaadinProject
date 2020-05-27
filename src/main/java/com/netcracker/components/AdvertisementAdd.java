package com.netcracker.components;

import com.netcracker.data.AdvertisementImage;
import com.netcracker.dto.AdvertisementAddBinder;
import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.netcracker.view.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;

@CssImport("./styles/advertisement-add.css")
public class AdvertisementAdd extends VerticalLayout {

    //private AdvertisementDTO advertisementDTO = new AdvertisementDTO();
    Label headAdvertisement = new Label("Create new Advertisement");
    TextField name = new TextField("Title");
    TextField description = new TextField("Description");
    TextField price = new TextField("Price");
    ImagesField imagesField = new ImagesField();

    UserService userService;

    Button submitButton = new Button("Create Advertisement");

    private BeanValidationBinder<AdvertisementAddBinder> binder;

    public  AdvertisementAdd(UserDTO user, FeignUserService feignUserService,
                            UserService userService, Dialog dialog, String qp){
        addClassName("advertisement-add");
        this.userService=userService;
        binder= new BeanValidationBinder<>(AdvertisementAddBinder.class);
        headAdvertisement.addClassName("adv-head");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setMaxWidth("800");
        submitButton.setSizeFull();

        name.setMaxWidth("800");
        name.setSizeFull();

        description.setMaxWidth("800");
        description.setSizeFull();

        price.setMaxWidth("800");
        price.setSizeFull();

        submitButton.addClickListener(buttonClickEvent -> {
            AdvertisementDTO  advertisement= new AdvertisementDTO();
            String token = userService.getCookieByName("Authorization");

            advertisement.setCategory_id(Long.parseLong(qp));
            advertisement.setUser_id(user.getId());
            advertisement.setName(name.getValue());
            advertisement.setDescription(description.getValue());
            advertisement.setDate(new Date());
            advertisement.setPrice(Double.parseDouble(price.getValue()));
            advertisement.setUrls(new ArrayList<>());

            imagesField.getImages().forEach(advertisementImage -> {
                advertisement.getUrls().add(feignUserService.addImage(token,advertisementImage).getBody());
            });
           if (feignUserService.addAdvertisement(token,advertisement).getStatusCode()== HttpStatus.OK){
               imagesField.getImages().clear();
               dialog.close();
           }
        });
        Div wrapper = new Div();
        wrapper.add(headAdvertisement);

        VerticalLayout head = new VerticalLayout(name,description,price);
        head.setMaxWidth("800");
        head.setSizeFull();
        wrapper.add(head,imagesField,submitButton);
        wrapper.setMaxWidth("970");
        wrapper.setSizeFull();
        wrapper.addClassName("adv-add-wrapper");

        add(wrapper);

        binder.forField(name).withValidator(this::validateText).asRequired().bind("name");
        binder.forField(description).withValidator(this::validateText).asRequired().bind("description");
        binder.forField(price).asRequired("Invalid number format").bind("price");

    }

    private ValidationResult validateText(String text, ValueContext ctx) {

        String errorMsg = userService.validateText(text);

        if (errorMsg == null) {
            return ValidationResult.ok();
        }

        return ValidationResult.error(errorMsg);
    }

}

package com.netcracker.components;

import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.MiniAdvertisement;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@CssImport("./styles/advertisement-list.css")
public class AdvertisementsList extends VerticalLayout {


     TextField search = new TextField();
     Button searchButton = new Button("Search");
     Button advertisementButton = new Button("Add Advertisement");
     Dialog addAdvertisements = new Dialog();

     List<AdvertisementDTO> advertisements;
     private List<MiniAdvertisement> miniAdvertisementFields;

     private FeignUserService feignUserService;
     private UserService userService;
     private UserDTO userDTO;

    public AdvertisementsList(FeignUserService feignUserService, UserService userService,
                              UserDTO userDTO, List<AdvertisementDTO> advertisements,String qp){
        this.feignUserService=feignUserService;
        this.userService=userService;
        this.advertisements=advertisements;
        this.userDTO=userDTO;

        advertisementButton.addClickListener(buttonClickEvent -> {
            addAdvertisements.add( new AdvertisementAdd(userDTO,feignUserService,userService,addAdvertisements,qp));
            addAdvertisements.open();
        });

        advertisementButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addAdvertisements.addDialogCloseActionListener(dialogCloseActionEvent -> {
            addAdvertisements.close();
            addAdvertisements.removeAll();
        });

        loadAdvertisements(qp);
    }
    public void loadAdvertisements(final String qp){
        this.removeAll();
        if(StringUtils.isEmpty(qp)) return;
        addClassName("advertisements-list");

        search.setPlaceholder("Search whatever you want...");
        search.setMaxWidth("400");
        search.setWidthFull();
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.getStyle().set("padding-left","20px");

        advertisementButton.addClassName("advertisement-add-button");
        try{
            this.advertisements=feignUserService.getAdvertisements(userService.getCookieByName("Authorization"),qp).getBody();
            miniAdvertisementFields = this.advertisements.stream().map(MiniAdvertisement::new)
                    .collect(Collectors.toList());
        }
       catch (FeignException.Forbidden e){}
        Div wrapper = new Div();
        wrapper.add(search,searchButton,advertisementButton);
        wrapper.addClassName("advertisements-head");
        wrapper.setSizeFull();
        add(wrapper,addAdvertisements);


        miniAdvertisementFields.forEach(this::add);
    }

}

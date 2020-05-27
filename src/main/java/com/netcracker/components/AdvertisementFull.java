package com.netcracker.components;

import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.CategoryService;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class AdvertisementFull extends VerticalLayout {

    private UserDTO user;

    private String queryParam;

    private UserService userService;

    private FeignUserService feign;

    private String token;

    private AdvertisementDTO advertisementDTO;

    AdvertisementFull advertisementFull;



    public AdvertisementFull(FeignUserService feignUserService, UserService userService,
                             CategoryService categoryService, UserDTO userDTO){
        this.userService=userService;
        this.feign=feignUserService;
        this.user=userDTO;







    }



    private void loadAdvertisement(){

    }
}

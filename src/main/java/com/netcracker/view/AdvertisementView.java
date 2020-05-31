package com.netcracker.view;

import com.netcracker.components.AdvertisementFull;
import com.netcracker.components.AppHeader;
import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.CategoryService;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Route("advertisement")
public class AdvertisementView extends VerticalLayout implements HasUrlParameter<String> {
    private UserDTO user;

    private String queryParam;

    private UserService userService;

    private FeignUserService feign;

    private String token;

    private AdvertisementDTO advertisementDTO;

    AdvertisementFull advertisementFull;

    public AdvertisementView(@Autowired FeignUserService feignUserService,
                             @Autowired UserService userService, @Autowired CategoryService categoryService){
        addClassName("advertisement-full");
        this.userService=userService;
        this.feign=feignUserService;

        try {
            token = userService.getCookieByName("Authentication");
            user = feign.getUserInfo(token).getBody();
        }
        catch (FeignException.Forbidden e ){
            UI.getCurrent().getPage().setLocation("login");
        }
        if (user != null) {

            this.advertisementFull = new AdvertisementFull(this.feign, this.userService, categoryService, user);


            VerticalLayout functional = new VerticalLayout();


            add(new AppHeader(userService, user));
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent,@OptionalParameter String qp) {
        if (user != null) {
            Location location = beforeEvent.getLocation();
            QueryParameters queryParameters = location.getQueryParameters();

            Map<String, List<String>> parametersMap = queryParameters.getParameters();

            List<String> qparams = parametersMap.get("id");
            if (qparams == null) UI.getCurrent().getPage().setLocation("mainPage");
            else queryParam = qparams.get(0);


            advertisementDTO = feign.getAdvertisement(token, queryParam).getBody();
        }
    }
}

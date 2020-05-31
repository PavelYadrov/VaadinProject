package com.netcracker.view;

import com.netcracker.components.AdvertisementsList;
import com.netcracker.components.AppHeader;
import com.netcracker.components.CategoryList;
import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.CategoryService;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import feign.FeignException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Route("mainPage")
@CssImport("./styles/main-page.css")
@Data
public class MainView extends VerticalLayout implements HasUrlParameter<String> {

    private UserDTO user;

    private String queryParam;

    private String searchParam;

    private UserService userService;

    private FeignUserService feign;

    private String token;

    private List<AdvertisementDTO> advertisementDTOS;

    private AdvertisementsList advertisementsList;

    private CategoryList categoryList;

    private List<Long> list;

    public MainView(@Autowired FeignUserService feignUserService,
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
            this.advertisementsList = new AdvertisementsList(this.feign, this.userService, user, advertisementDTOS, queryParam, searchParam);
            advertisementsList.addClassName("advertisements-list");

            this.categoryList = new CategoryList(feignUserService, categoryService, userService, advertisementsList, queryParam, user);
            categoryList.addClassName("category-list");

            Div content = new Div(categoryList, advertisementsList);
            content.addClassName("content");
            content.setSizeFull();
            add(new AppHeader(userService, user));
            add(content);
            addClassName("main-view");
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent,@OptionalParameter String s) {
        if (user != null) {
            Location location = beforeEvent.getLocation();
            QueryParameters queryParameters = location.getQueryParameters();

            Map<String, List<String>> parametersMap =
                    queryParameters.getParameters();

            List<String> qparams = parametersMap.get("category_id");
            if (qparams == null) queryParam = "1";
            else queryParam = qparams.get(0);
            List<String> searchParams = parametersMap.get("search");
            if (searchParams == null) searchParam = "";
            else searchParam = searchParams.get(0);
            try {
                advertisementsList.loadAdvertisements(queryParam, searchParam);
            } catch (FeignException.Forbidden e) {
                UI.getCurrent().getPage().setLocation("login");
            }
            categoryList.setOpened(queryParam);
        }
    }

}

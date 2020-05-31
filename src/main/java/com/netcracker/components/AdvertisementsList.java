package com.netcracker.components;

import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.CustomPair;
import com.netcracker.dto.MiniAdvertisement;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Registration searchListener;

    public AdvertisementsList(FeignUserService feignUserService, UserService userService,
                              UserDTO userDTO, List<AdvertisementDTO> advertisements, String qp, String searchParam) {
        this.feignUserService = feignUserService;
        this.userService = userService;
        this.advertisements = advertisements;
        this.userDTO = userDTO;

        advertisementButton.addClickListener(buttonClickEvent -> {
            addAdvertisements.add(new AdvertisementAdd(userDTO, feignUserService, userService, addAdvertisements, qp));
            addAdvertisements.open();
        });

        advertisementButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addAdvertisements.addDialogCloseActionListener(dialogCloseActionEvent -> {
            addAdvertisements.close();
            addAdvertisements.removeAll();
        });


        loadAdvertisements(qp, searchParam);
    }

    public void loadAdvertisements(String qp, String searchParam) {
        if (searchListener != null) searchListener.remove();
        this.removeAll();
        if (StringUtils.isEmpty(qp)) return;
        addClassName("advertisements-list");

        search.setPlaceholder("Search whatever you want...");
        search.setWidthFull();
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClassName("search-button");

        searchListener = searchButton.addClickListener(buttonClickEvent -> {
            List<String> param1 = new ArrayList<>();
            List<String> param2 = new ArrayList<>();
            Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();
            param1.add(search.getValue());
            param2.add(qp);
            parametersMap.put("search", param1);
            parametersMap.put("category_id", param2);

            QueryParameters queryParameters = new QueryParameters(parametersMap);
            UI.getCurrent().navigate("mainPage", queryParameters);
        });

        if (searchParam == null || searchParam.isEmpty()) {
            this.advertisements = feignUserService.getAdvertisements(userService.getCookieByName("Authentication"), qp).getBody();
        } else {
            CustomPair pair = new CustomPair();
            pair.setFirstLine(qp);
            pair.setSecondLine(searchParam);
            this.advertisements = feignUserService.getAdvertisementsBySearch(userService.getCookieByName("Authentication"), pair).getBody();
        }
        miniAdvertisementFields = this.advertisements.stream().map(MiniAdvertisement::new)
                .collect(Collectors.toList());

        Div wrapper = new Div();
        wrapper.add(search, advertisementButton, searchButton);
        wrapper.addClassName("advertisements-head");
        wrapper.setSizeFull();
        add(wrapper, addAdvertisements);


        miniAdvertisementFields.forEach(this::add);
    }

}

package com.netcracker.components;

import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.CustomPair;
import com.netcracker.dto.MainPageParams;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

    private String token;

    private TextField search = new TextField();
    private Button searchButton = new Button("Search");
    private Button advertisementButton = new Button("Add Advertisement");
    private Registration advButtonListener;
    private Dialog addAdvertisements = new Dialog();
    private Button nextPage = new Button("Next Page");
    private Registration nextPageListener;
    private Button prevPage = new Button("Previous Page");
    private Registration prevPageListener;
    private Span info = new Span();
    private Div pageInfo = new Div();
    private HorizontalLayout bottom = new HorizontalLayout();

    private List<AdvertisementDTO> advertisements;
    private List<MiniAdvertisement> miniAdvertisementFields;

    private FeignUserService feignUserService;
    private UserService userService;
    private UserDTO userDTO;
    private Registration searchListener;

    private String queryParam;


    public AdvertisementsList(FeignUserService feignUserService, UserService userService, String pageParam,
                              UserDTO userDTO, List<AdvertisementDTO> advertisements, String qp, String searchParam) {
        this.feignUserService = feignUserService;
        this.userService = userService;
        this.advertisements = advertisements;
        this.userDTO = userDTO;


        advertisementButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        advertisementButton.getStyle().set("cursor", "pointer");

        addAdvertisements.addDialogCloseActionListener(dialogCloseActionEvent -> {
            addAdvertisements.close();
            addAdvertisements.removeAll();
        });

        nextPage.addClassName("next-page");
        prevPage.addClassName("prev-page");
        info.addClassName("info");
        pageInfo.addClassName("page-info");
        bottom.addClassName("bottom");
        bottom.setSizeFull();

        loadAdvertisements(qp, searchParam, pageParam);
    }

    public void loadAdvertisements(String qp, String searchParam, String pageParam) {
        if (searchListener != null) searchListener.remove();
        if (nextPageListener != null) nextPageListener.remove();
        if (prevPageListener != null) prevPageListener.remove();
        if (advButtonListener != null) advButtonListener.remove();

        this.removeAll();
        if (StringUtils.isEmpty(qp)) return;
        addClassName("advertisements-list");
        MainPageParams params = new MainPageParams();
        params.setCategory_id(qp);
        params.setSearch(searchParam);
        params.setPage(pageParam);

        advButtonListener = advertisementButton.addClickListener(buttonClickEvent -> {
            addAdvertisements.add(new AdvertisementAdd(userDTO, feignUserService, userService, addAdvertisements, qp));
            addAdvertisements.open();
        });
        token = userService.getCookieByName("Authentication");

        search.setPlaceholder("Search whatever you want...");
        search.setWidthFull();
        search.setValue(searchParam);
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClassName("search-button");
        searchButton.addClickShortcut(Key.ENTER);

        searchListener = searchButton.addClickListener(buttonClickEvent -> this.setParams(qp, pageParam, 0L));

        if (searchParam == null || searchParam.isEmpty()) {
            CustomPair pair = new CustomPair();
            pair.setFirstLine(qp);
            pair.setSecondLine(pageParam);
            this.advertisements = feignUserService.getAdvertisements(token, pair).getBody();
        } else {
            this.advertisements = feignUserService.getAdvertisementsBySearch(token, params).getBody();
        }
        miniAdvertisementFields = this.advertisements.stream().map(MiniAdvertisement::new)
                .collect(Collectors.toList());

        Div wrapper = new Div();
        wrapper.add(search, advertisementButton, searchButton);
        wrapper.addClassName("advertisements-head");
        wrapper.setSizeFull();
        add(wrapper, addAdvertisements);
        miniAdvertisementFields.forEach(this::add);

        nextPage.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        prevPage.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        nextPageListener = nextPage.addClickListener(buttonClickEvent -> {
            this.setParams(qp, pageParam, 1L);
        });

        prevPageListener = prevPage.addClickListener(buttonClickEvent -> {
            this.setParams(qp, pageParam, -1L);
        });

        Long maxAdv = Long.parseLong(pageParam) * 10 - 10 + miniAdvertisementFields.size();
        Long minAdv;
        if (miniAdvertisementFields.isEmpty()) minAdv = 0L;
        else minAdv = Long.parseLong(pageParam) * 10 - 9;

        pageInfo.setText("Page: " + pageParam + " / Advertisements: " + minAdv + "-" + maxAdv + " of " + feignUserService.getCount(token, params).getBody());
        pageInfo.setSizeFull();
        bottom.add(prevPage, pageInfo, nextPage);
        add(bottom);

        if (pageParam.equals("1")) prevPage.onEnabledStateChanged(false);
        else prevPage.onEnabledStateChanged(true);

        if (miniAdvertisementFields.size() < 10) nextPage.onEnabledStateChanged(false);
        else nextPage.onEnabledStateChanged(true);
    }

    private void setParams(String qp, String page, Long shift) {
        Long shiftedParam = (Long.parseLong(page) + shift);
        List<String> param1 = new ArrayList<>();
        List<String> param2 = new ArrayList<>();
        List<String> param3 = new ArrayList<>();
        Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();
        param1.add(search.getValue());
        param2.add(qp);
        if (shift > 0) {
            if (miniAdvertisementFields.isEmpty() || miniAdvertisementFields.size() < 10) {
                param3.add(page);
            } else param3.add(shiftedParam.toString());
        }
        if (shift < 0) {
            if (shiftedParam <= 0) {
                param3.add(page);
            } else param3.add(shiftedParam.toString());
        }
        if (shift == 0) param3.add(page);

        parametersMap.put("search", param1);
        parametersMap.put("category_id", param2);
        parametersMap.put("page", param3);

        QueryParameters queryParameters = new QueryParameters(parametersMap);
        UI.getCurrent().navigate("mainPage", queryParameters);
    }


}

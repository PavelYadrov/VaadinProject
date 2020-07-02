package com.netcracker.components;

import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.QueryParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@CssImport("./styles/mini-components.css")
public class MiniAdvertisement extends HorizontalLayout {

    private String imageRoute;

    private Image currentImage;
    private Span name;
    private Span description;
    private Span price;
    private Span date;

    private Span thirdLine = new Span();

    public MiniAdvertisement(AdvertisementDTO advertisement, UserService userService, FeignUserService feign, String token) {
        imageRoute = feign.getImageUrl(token).getBody();
        if (advertisement.getUrls().isEmpty()) {
            currentImage = new Image(imageRoute + "no-image.png", "faceImage");
        } else {
            currentImage = new Image(imageRoute + advertisement.getUrls().get(0), "faceImage");
        }
        currentImage.setMaxHeight("100px");
        currentImage.setMaxWidth("100px");
        currentImage.addClassName("mini-adv-image");

        name = new Span(advertisement.getName());
        name.addClassName("name");

        if (advertisement.getDescription().length() >= 50) {
            description = new Span(advertisement.getDescription().substring(0, 50) + "...");
        } else description = new Span(advertisement.getDescription());
        description.addClassName("description");


        if (advertisement.getPrice().toString().equals("0.0")) {
            price = new Span("Price: FREE!");
        } else {
            price = new Span(advertisement.getPrice().toString() + "$");
        }
        price.addClassName("price");

        date = new Span(advertisement.getDate().toLocaleString().substring(0, advertisement.getDate().toLocaleString().length() - 3));
        date.addClassName("date");

        HorizontalLayout firstLine = new HorizontalLayout(name, price);
        firstLine.addClassName("firstLine");
        firstLine.setSizeFull();

        HorizontalLayout secondLine = new HorizontalLayout(description, date);
        secondLine.addClassName("secondLine");
        secondLine.setSizeFull();

        VerticalLayout verticalLayout = new VerticalLayout(firstLine, secondLine);
        verticalLayout.setMaxWidth("850");
        verticalLayout.getStyle().set("padding-left", "5px");
        verticalLayout.setSizeFull();

        add(currentImage, verticalLayout);

        this.addClassName("mini-adv");
        setWidthFull();

        this.addClickListener(formLayoutClickEvent -> {
            List<String> param = new ArrayList<>();
            Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();
            param.add(advertisement.getId().toString());

            parametersMap.put("id", param);

            QueryParameters qp = new QueryParameters(parametersMap);
            UI.getCurrent().navigate("advertisement", qp);
        });

    }

}

package com.netcracker.dto;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.QueryParameters;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@CssImport("./styles/mini-adv.css")
public class MiniAdvertisement extends HorizontalLayout {

    private String imageRoute = "http://localhost:8090/images/";

    private Image currentImage;
    private Span name;
    private Span description;
    private Span price;
    private Span date;

    private Span thirdLine = new Span();

    public MiniAdvertisement(AdvertisementDTO advertisement){
        if (advertisement.getUrls().isEmpty()) {
            currentImage = new Image(imageRoute + "no-image.png", "faceImage");
        } else {
            currentImage = new Image(imageRoute + advertisement.getUrls().get(0), "faceImage");
        }
        currentImage.setMaxHeight("100px");
        currentImage.setMaxWidth("100px");

        name = new Span(advertisement.getName());
        name.addClassName("name");

        //TODO add substring for description - optional
        description = new Span("Description: " + advertisement.getDescription());
        description.addClassName("description");


        if (advertisement.getPrice().toString().equals("0.0")) {
            price = new Span("Price: FREE!");
        } else {
            price = new Span("Price: " + advertisement.getPrice().toString() + "$");
        }
        price.addClassName("price");

        date = new Span("Registration date: " + advertisement.getDate().toLocaleString().substring(0, advertisement.getDate().toLocaleString().length() - 3));
        date.addClassName("date");

        thirdLine.add(price, date);
        thirdLine.addClassName("third-line");

        VerticalLayout verticalLayout = new VerticalLayout(name, description, thirdLine);
        verticalLayout.setMaxWidth("850");
        verticalLayout.setSizeFull();

        add(currentImage, verticalLayout);

        this.addClassName("mini-adv");
        setWidthFull();

        //TODO Add redirect to advertisement page
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

package com.netcracker.view;

import com.netcracker.components.AdvertisementUpdate;
import com.netcracker.components.AppHeader;
import com.netcracker.dto.AdvertisementDTO;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.CategoryService;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("advertisement")
@CssImport(value = "./styles/advertisement-page.css")
public class AdvertisementView extends VerticalLayout implements HasUrlParameter<String> {
    private UserDTO user;

    private String queryParam;

    private UserService userService;

    private FeignUserService feign;

    private String token;

    private AdvertisementDTO advertisementDTO;

    private Image currentImage = new Image();

    private String imageRoute = "http://localhost:8090/images/";

    private HorizontalLayout firstImagesLayer = new HorizontalLayout();
    private HorizontalLayout secondImagesLayer = new HorizontalLayout();
    private VerticalLayout images = new VerticalLayout();
    private VerticalLayout info = new VerticalLayout();

    private Span title = new Span();
    private Span price = new Span();
    private Span description = new Span();
    private Span person = new Span();

    private Button delete = new Button("Delete");
    private Button update = new Button("Update");
    private Button message = new Button("Send Message");

    private Registration deleteListener;
    private Registration updateListener;
    private Registration personListener;

    private Dialog changeAdvertisement = new Dialog();

    public AdvertisementView(@Autowired FeignUserService feignUserService,
                             @Autowired UserService userService, @Autowired CategoryService categoryService) {
        addClassName("content");
        this.userService = userService;
        this.feign = feignUserService;

        try {
            token = userService.getCookieByName("Authentication");
            user = feign.getUserInfo(token).getBody();
        } catch (FeignException.Forbidden e) {
            UI.getCurrent().getPage().setLocation("login");
        }
        if (user != null) {
            currentImage.setMaxWidth("600px");
            currentImage.setMaxHeight("600px");
            currentImage.setMinWidth("600px");
            currentImage.setMinHeight("600px");
            currentImage.setSizeFull();

            title.addClassName("title-adv");
            price.addClassName("price-adv");
            description.addClassName("description-adv");
            person.addClassName("person-adv");

            message.getStyle().set("margin-left", "20px");
            message.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            delete.getStyle().set("margin-left", "20px");
            update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            update.getStyle().set("margin-left", "20px");


            firstImagesLayer.setMaxWidth("600px");
            firstImagesLayer.setMaxHeight("150px");
            firstImagesLayer.getStyle().set("display", "inline-table");
            secondImagesLayer.setMaxWidth("600px");
            secondImagesLayer.setMaxHeight("150px");
            secondImagesLayer.getStyle().set("display", "inline-table");
            secondImagesLayer.getStyle().set("margin-top", "3px");
            firstImagesLayer.setSizeFull();
            secondImagesLayer.setSizeFull();

            images.addClassName("images");
            info.addClassName("info");
            info.setMaxWidth("600px");
            info.setMinWidth("600px");
            info.setSizeFull();

            changeAdvertisement.addDialogCloseActionListener(dialogCloseActionEvent -> {
                changeAdvertisement.close();
                changeAdvertisement.removeAll();
            });
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String qp) {
        if (user != null) {
            queryParam = userService.setParam(beforeEvent, qp);
            if (queryParam == null) {
                UI.getCurrent().getPage().setLocation("mainPage");
                return;
            }

            ResponseEntity<AdvertisementDTO> response = feign.getAdvertisement(token, queryParam);
            if (response.getStatusCode() != HttpStatus.OK) {
                UI.getCurrent().getPage().setLocation("mainPage");
            } else {
                advertisementDTO = response.getBody();
                this.loadAdvertisement(advertisementDTO);
            }
        } else UI.getCurrent().getPage().setLocation("mainPage");
    }

    private void loadAdvertisement(AdvertisementDTO advertisementDTO) {
        this.removeAll();

        add(new AppHeader(true, user));
        Div content = new Div(images, info);
        content.addClassName("content-adv");
        add(content);
        add(changeAdvertisement);

        if (deleteListener != null) deleteListener.remove();
        if (updateListener != null) updateListener.remove();
        if (personListener != null) personListener.remove();

        title.setText(advertisementDTO.getName());
        if (advertisementDTO.getPrice().equals(0.0)) {
            price.setText("Price: FREE!");
        } else price.setText("Price: " + advertisementDTO.getPrice() + "$");
        Div firstLine = new Div(title, price);
        firstLine.addClassName("firstLine");
        firstLine.setSizeFull();

        info.add(firstLine);

        if (advertisementDTO.getUrls().isEmpty()) {
            currentImage.setSrc(imageRoute + "no-image.png");
            images.add(currentImage);
        } else {
            currentImage.setSrc(imageRoute + advertisementDTO.getUrls().get(0));
            currentImage.setAlt("faceImage");
            images.add(currentImage);
            advertisementDTO.getUrls().remove(0);

            int size = advertisementDTO.getUrls().size();
            String indent;

            if (size > 4) {
                for (int i = 0; i < 4; i++) {
                    if (i == 0) firstImagesLayer.add(setImage(advertisementDTO.getUrls().get(0), "16px"));
                    else firstImagesLayer.add(setImage(advertisementDTO.getUrls().get(0), "0px"));
                    advertisementDTO.getUrls().remove(0);
                }
                size -= 4;
                images.add(firstImagesLayer);
            }
            switch (size) {
                case 4:
                    indent = "0px";
                    break;
                case 3:
                    indent = "37.5px";
                    break;
                case 2:
                    indent = "100px";
                    break;
                case 1:
                    indent = "225px";
                    break;
                default:
                    indent = "";
            }
            if (!advertisementDTO.getUrls().isEmpty()) {
                int i = 0;
                Double firstIndent = Double.parseDouble(indent.substring(0, indent.length() - 2)) + 16;
                for (String url : advertisementDTO.getUrls()) {
                    if (i == 0) secondImagesLayer.add(setImage(url, firstIndent.toString() + "px"));
                    else secondImagesLayer.add(setImage(url, indent));
                    i++;
                }
            }
            images.add(secondImagesLayer);
        }

        description.setText("Description: " + advertisementDTO.getDescription());
        description.getStyle().set("padding-top", "10px");
        info.add(description);

        Span fullName = new Span(advertisementDTO.getLastName() + " " + advertisementDTO.getFirstName());
        fullName.addClassName("full-name");

        person.setText("Owner: ");
        personListener = fullName.addClickListener(spanClickEvent -> {
            List<String> param1 = new ArrayList<>();
            Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();
            param1.add(advertisementDTO.getUser_id().toString());

            parametersMap.put("id", param1);

            QueryParameters qp = new QueryParameters(parametersMap);
            UI.getCurrent().navigate("user", qp);
        });
        Div owner = new Div();
        owner.add(person, fullName, message);
        info.add(owner);
        try {
            feign.roleCheck(token);
            setTools(true);
        } catch (FeignException.Forbidden e) {
            if (user.getId().equals(advertisementDTO.getUser_id())) {
                setTools(false);
            }
        }

    }

    private Image setImage(String url, String indent) {
        Image image = new Image(imageRoute + url, "name");
        image.setMaxWidth("150px");
        image.setMinWidth("150px");
        image.setMaxHeight("150px");
        image.setMinHeight("150px");
        image.setSizeFull();
        image.getStyle().set("padding-left", indent);
        image.getStyle().set("margin", "0");
        image.addClickListener(imageClickEvent -> {
            String imgUrl = image.getSrc();
            image.setSrc(currentImage.getSrc());
            currentImage.setSrc(imgUrl);
        });
        return image;
    }

    private void setTools(Boolean adminFlag) {
        Div tools = new Div();
        Span span = new Span("Change Advertisement");
        span.addClassName("change-adv");
        tools.add(span, delete, update);
        info.add(tools);
        if (adminFlag) {
            deleteListener = delete.addClickListener(buttonClickEvent -> {
                if (feign.adminDeleteAdvertisement(token, advertisementDTO.getId().toString()).getStatusCode() == HttpStatus.OK) {
                    UI.getCurrent().getPage().setLocation("mainPage");
                }
            });
        } else {
            deleteListener = delete.addClickListener(buttonClickEvent -> {
                if (feign.deleteAdvertisement(token, advertisementDTO.getId().toString()).getStatusCode() == HttpStatus.OK) {
                    UI.getCurrent().getPage().setLocation("mainPage");
                }
            });
        }
        updateListener = update.addClickListener(buttonClickEvent -> {
            changeAdvertisement.add(new AdvertisementUpdate(user, feign, adminFlag, userService, changeAdvertisement, advertisementDTO));
            changeAdvertisement.open();
        });
    }
}

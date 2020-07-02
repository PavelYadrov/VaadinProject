package com.netcracker.components;

import com.netcracker.dto.MessageDTO;
import com.netcracker.dto.RoomDTO;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
@CssImport("./styles/mini-components.css")
public class MiniChat extends HorizontalLayout {
    private String imageRoute;

    private Long roomId;

    private Image currentImage = new Image();
    private HorizontalLayout firstLine = new HorizontalLayout();
    private HorizontalLayout secondLine = new HorizontalLayout();
    private Span username = new Span();
    private Span lastUpdate = new Span();
    private Span lastMessage = new Span();
    private Icon icon;
    private Date lastUpdateTime;

    private RoomDTO room;

    public MiniChat(RoomDTO roomDTO, UserService userService) {
        addClassName("mini-chat");

        this.room = roomDTO;

        currentImage.setSrc(imageRoute + roomDTO.getUrl());
        currentImage.setWidth("100px");
        currentImage.setHeight("80px");
        currentImage.addClassName("mini-image");

        firstLine.addClassName("firstLine");
        secondLine.addClassName("secondLine");
        username.addClassName("chat-username");
        lastUpdate.addClassName("last-update");
        lastMessage.addClassName("last-message");

        imageRoute = userService.serviceUrl() + "images/";

        firstLine.add(username);
        secondLine.add(lastMessage, lastUpdate);

        username.setText(room.getFirstName() + " " + room.getLastName());
        lastUpdate.setText(room.getLastUpdate().toLocaleString().substring(0, room.getLastUpdate().toLocaleString().length() - 3));
        lastUpdateTime = room.getLastUpdate();
        if (room.getText().length() > 10) {
            lastMessage.setText(room.getText().substring(0, 10) + "...");
        } else lastMessage.setText(room.getText());

        VerticalLayout verticalLayout = new VerticalLayout(firstLine, secondLine);
        verticalLayout.setMaxWidth("650");
        verticalLayout.getStyle().set("padding-left", "5px");
        verticalLayout.setSizeFull();

        this.addClickListener(horizontalLayoutClickEvent -> {
            if (icon != null) {
                firstLine.remove(icon);
                icon = null;
            }
        });

        if (roomDTO.getUnread()) {
            icon = new Icon(VaadinIcon.CIRCLE);
            icon.setColor("red");
            firstLine.add(icon);
            icon.getStyle().set("float", "right");
        }
        add(currentImage, verticalLayout);
    }

    public void addIcon() {
        if (icon == null) {
            icon = new Icon(VaadinIcon.CIRCLE);
            icon.setColor("red");
            firstLine.add(icon);
            icon.getStyle().set("float", "right");
        }
    }

    public void setNewInfo(MessageDTO message) {
        lastUpdateTime = message.getMessageDate();
        lastUpdate.setText(lastUpdateTime.toLocaleString().substring(0, lastUpdateTime.toLocaleString().length() - 3));

        if (message.getText().length() > 10) {
            lastMessage.setText(message.getText().substring(0, 10) + "...");
        } else lastMessage.setText(message.getText());
    }
}


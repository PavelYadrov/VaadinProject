package com.netcracker.components;

import com.netcracker.dto.CustomPair;
import com.netcracker.dto.MessageDTO;
import com.netcracker.dto.RoomDTO;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.FeignUserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.util.List;

@CssImport("./styles/chat.css")
public class Chat extends VerticalLayout {

    private String nativeJSScroll = "document.querySelector(\".message-window\").scrollTo(0,document.querySelector(\".message-window\").scrollHeight)";

    private final UnicastProcessor<ChatEvent> publisher;
    private final Flux<ChatEvent> messagesFlux;

    private UserDTO user;

    private RoomDTO room;

    private String imageRoute = "http://localhost:8090/images/";

    private HorizontalLayout head = new HorizontalLayout();
    private Image userAvatar = new Image();
    private Span username = new Span();

    private VerticalLayout messagesWindow = new VerticalLayout();

    private HorizontalLayout bottom = new HorizontalLayout();
    private TextField message = new TextField();
    private Button sendMessage = new Button("send");
    private Registration messageListener;

    private List<MessageDTO> messages;

    private FeignUserService feign;

    public Chat(@Autowired FeignUserService feignUserService, UserDTO userDTO, UnicastProcessor<ChatEvent> publisher,
                Flux<ChatEvent> messages) {
        this.feign = feignUserService;
        this.user = userDTO;
        this.publisher = publisher;
        this.messagesFlux = messages;

        addClassName("chat-main");
        setSizeFull();

        sendMessage.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        head.getElement().getThemeList().add("dark");
        head.addClassName("chat-header");

        userAvatar.setMaxHeight("80px");
        userAvatar.setMaxWidth("100px");
        userAvatar.addClassName("mini-image");

        username.addClassName("main-chat-username");

        messagesWindow.setHeight("450px");
        messagesWindow.setWidth("100%");
        messagesWindow.getStyle().set("overflow-y", "auto");
        messagesWindow.addClassName("message-window");


    }

    public void loadChat(RoomDTO roomDTO, String token) {
        this.removeAll();
        messagesWindow.removeAll();
        if (messageListener != null) messageListener.remove();

        if (roomDTO != null) {
            sendMessage.onEnabledStateChanged(true);
            this.room = roomDTO;
            userAvatar.setSrc(imageRoute + roomDTO.getUrl());
            username.setText(room.getFirstName() + " " + room.getLastName());
            this.messages = feign.getRoomMessages(token, room.getId().toString()).getBody();
            messageListener = sendMessage.addClickListener(buttonClickEvent -> {
                if (!message.getValue().isEmpty()) {
                    CustomPair pair = new CustomPair();
                    pair.setFirstLine(roomDTO.getUserId().toString());
                    pair.setSecondLine(message.getValue());
                    MessageDTO mess = feign.receiveMessage(token, pair).getBody();
                    newMessage(mess);

                    publisher.onNext(new ChatEvent(mess));
                }
            });
            if (!messages.isEmpty()) {
                messages.forEach(this::newMessage);
            }
        } else {
            username.setText("Choose dialog to start chat");
            sendMessage.onEnabledStateChanged(false);
        }
        head.add(userAvatar, username);

        add(head);
        add(messagesWindow);

        bottom.add(message, sendMessage);
        bottom.setWidth("100%");
        bottom.expand(message);
        add(bottom);

    }

    public Div newMessage(MessageDTO messageDTO) {
        Div message = new Div();

        Div messageSender = new Div();
        messageSender.add(messageDTO.getFirstName() + " " + messageDTO.getLastName());

        Div text = new Div();
        text.add(messageDTO.getText());

        Div sendDate = new Div();
        sendDate.add(messageDTO.getMessageDate().toLocaleString().substring(0, messageDTO.getMessageDate().toLocaleString().length() - 3));
        message.add(messageSender, text, sendDate);
        if (!messageDTO.getSenderId().equals(user.getId())) {
            message.getStyle().set("float", "left");
            message.getElement().getThemeList().add("blue");
            message.addClassName("message-receiver");
            sendDate.addClassName("message-receiver-date");
        } else {
            message.getStyle().set("margin-left", "auto");
            message.getElement().getThemeList().add("dark");
            message.addClassName("message-sender");
            text.getStyle().set("text-align", "left");
            sendDate.addClassName("message-sender-date");
            messageSender.getStyle().set("text-align", "right");
        }
        messagesWindow.add(message);
        UI.getCurrent().getPage().executeJs(nativeJSScroll);
        return message;
    }
}

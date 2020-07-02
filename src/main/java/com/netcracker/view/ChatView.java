package com.netcracker.view;


import com.netcracker.components.AppHeader;
import com.netcracker.components.Chat;
import com.netcracker.components.ChatEvent;
import com.netcracker.components.MiniChat;
import com.netcracker.dto.RoomDTO;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.Route;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.util.List;
import java.util.stream.Collectors;

@Push
@Route("chat")
@CssImport("./styles/chat-view.css")
public class ChatView extends VerticalLayout implements BeforeLeaveObserver {

    private final UnicastProcessor<ChatEvent> publisher;
    private final Flux<ChatEvent> messages;

    private UserDTO user;

    private String queryParam;

    private UserService userService;

    private FeignUserService feign;

    private String token;

    private List<RoomDTO> rooms;
    private List<MiniChat> miniRooms;
    private List<Long> roomIds;
    private MiniChat uploadChat;
    private VerticalLayout dialogs = new VerticalLayout();

    private Chat chat;

    private Long uploadChatId;

    public ChatView(@Autowired FeignUserService feignUserService, @Autowired UserService userService,
                    UnicastProcessor<ChatEvent> publisher, Flux<ChatEvent> messages) {

        this.userService = userService;
        this.feign = feignUserService;
        this.publisher = publisher;
        this.messages = messages;

        try {
            token = userService.getCookieByName("Authentication");
            user = feign.getUserInfo(token).getBody();
        } catch (FeignException.Forbidden e) {
            UI.getCurrent().getPage().setLocation("login");
        }
        if (user != null) {

            addClassName("chat-view");

            dialogs.addClassName("chat-dialogs");
            dialogs.setMaxWidth("650px");
            dialogs.setSizeFull();
            dialogs.setMaxHeight("600px");
            dialogs.getStyle().set("overflow", "auto");

            rooms = feign.getUserRooms(token).getBody();

            loadChats();
        } else {
            UI.getCurrent().getPage().setLocation("mainPage");
        }

    }

    private void loadChats() {
        this.removeAll();
        uploadChatId = 0L;

        miniRooms = rooms.stream().map(roomDTO -> {
            MiniChat miniChat = setMiniChat(roomDTO);
            dialogs.add(miniChat);
            return miniChat;
        }).collect(Collectors.toList());

        if (!miniRooms.isEmpty()) {
            this.chat = new Chat(feign, user, publisher, userService);
            chat.loadChat(null, token);
            chat.addClassName("chat-main");
            chat.setMaxWidth("900px");
            chat.setSizeFull();

            roomIds = miniRooms.stream()
                    .map(MiniChat::getRoomId)
                    .collect(Collectors.toList());

            add(new AppHeader(true, false, true, user, feign, userService));
            Div content = new Div(dialogs, chat);
            content.addClassName("content-chat");
            add(content);
        } else {
            UI.getCurrent().getPage().setLocation("mainPage");
        }
    }

    private MiniChat setMiniChat(RoomDTO roomDTO) {
        MiniChat miniChat = new MiniChat(roomDTO, userService);
        miniChat.setRoomId(roomDTO.getId());
        miniChat.addClickListener(horizontalLayoutClickEvent -> {
            chat.loadChat(roomDTO, token);
            uploadChatId = roomDTO.getId();
        });
        return miniChat;
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        uploadChatId = 0L;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        subscribe(attachEvent.getUI());
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    private void subscribe(UI ui) {
        messages.subscribe(message -> {
            if (!ui.isClosing()) {
                if (user.getId().equals(message.getMessage().getReceiverId())) {
                    ui.access(() -> {
                        if (message.getMessage().getRoomId().equals(uploadChatId)) {
                            chat.newMessage(message.getMessage());
                            uploadChat = miniRooms.stream()
                                    .filter(miniChat -> miniChat.getRoomId().equals(message.getMessage().getRoomId()))
                                    .collect(Collectors.toList()).get(0);
                            feign.setMessageRead(token, message.getMessage().getId().toString());

                            uploadChat.setNewInfo(message.getMessage());
                        } else {
                            if (roomIds.contains(message.getMessage().getRoomId())) {
                                uploadChat = miniRooms.stream()
                                        .filter(miniChat -> miniChat.getRoomId().equals(message.getMessage().getRoomId()))
                                        .collect(Collectors.toList()).get(0);
                                uploadChat.addIcon();
                                uploadChat.setNewInfo(message.getMessage());
                                dialogs.remove(uploadChat);
                                dialogs.addComponentAsFirst(uploadChat);
                            } else {
                                RoomDTO roomDTO = feign.getRoomById(token, message.getMessage().getRoomId().toString())
                                        .getBody();
                                if (roomDTO != null) {
                                    MiniChat miniChat = setMiniChat(roomDTO);
                                    miniChat.addIcon();
                                    miniRooms.add(miniChat);
                                    roomIds.add(roomDTO.getId());
                                    dialogs.addComponentAsFirst(miniChat);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}

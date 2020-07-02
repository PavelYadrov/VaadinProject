package com.netcracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class RoomDTO {

    private Long id;

    private Long userId;

    private String username;

    private Date lastUpdate;

    private String url;

    private String text;

    private String firstName;

    private String lastName;

    private Boolean unread;


}

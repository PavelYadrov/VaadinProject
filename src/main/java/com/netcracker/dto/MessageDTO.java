package com.netcracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class MessageDTO {
    private Long id;
    private String text;
    private String username;
    private Date messageDate;
    private Long senderId;
    private Long receiverId;
    private Long roomId;
    private Boolean read;
    private String firstName;
    private String lastName;
}

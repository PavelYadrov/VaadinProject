package com.netcracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class UserRegisterDTO {

    private Long id;

    @Length(min=6,max=40)
    private String password;


    @Length(min=4,max=30)
    private String username;

    private String firstName;


    private String lastName;

    @Email
    private String email;

    private Status status;

    private String avatar;
}
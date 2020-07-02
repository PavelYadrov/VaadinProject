package com.netcracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class AdvertisementDTO {

    private Long id;

    private Long user_id;

    private String firstName;

    private String lastName;

    private String name;

    private String description;

    private Double price;

    private Long category_id;

    private Date date;

    private List<String> urls;

    private String avatar;
}
package com.netcracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class AdvertisementAddBinder {

    @Length(min = 5, max = 100, message = "Title size must ve greater than 5 and less than 100 symbols")
    private String name;

    @Length(min = 10, max = 1000, message = "Description size must ve greater than 10 and less than 1000 symbols")
    private String description;


    private Double price;
}

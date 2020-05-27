package com.netcracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.NumberFormat;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class AdvertisementAddBinder {
    @Length(min = 5,max=100)
    @NonNull
    private String name;
    @NonNull
    @Length(min = 10,max=1000)
    private String description;
    @NonNull
    @NumberFormat
    private Double price;
}

package com.netcracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class AdvertisementAddBinder {

    @Length(min = 5, max = 50, message = "Title size must ve greater than 5 and less than 50 symbols")
    @NonNull
    private String name;

    @Length(min = 10, max = 1000, message = "Description size must ve greater than 10 and less than 1000 symbols")
    @NonNull
    private String description;
    @NonNull
    private String price;
}

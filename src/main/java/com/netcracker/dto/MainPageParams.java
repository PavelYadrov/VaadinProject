package com.netcracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MainPageParams {
    private String category_id;
    private String search;
    private String page;
}

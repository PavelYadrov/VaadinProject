package com.netcracker.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdvertisementImage {

    private byte[] value;
    private String extension;
    private String name;

}

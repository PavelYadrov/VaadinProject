package com.netcracker.service;

import com.netcracker.data.AdvertisementImage;
import com.netcracker.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Service
@FeignClient("resource")
public interface FeignUserService {

    @PostMapping (value = "api/auth/login")
    ResponseEntity<String> login(@RequestBody LoginForm loginForm);

    @PostMapping(value = "api/auth/register")
    ResponseEntity<String> registration(@RequestBody UserRegisterDTO userRegisterDTO);

    @GetMapping(value = "api/auth/info" )
    ResponseEntity<UserDTO> getUserInfo(@RequestHeader(name = "Authorization") String token);

    @PostMapping(value = "api/user/getFirstLayerCategories")
    ResponseEntity<List<CategoryDTO>> getFirstLayerCategories(@RequestHeader(name = "Authorization") String token,@RequestBody String id);

    @PostMapping(value = "api/user/getFirstLayerCategories")
    ResponseEntity<List<CategoryDTO>> getCategories(@RequestHeader(name = "Authorization") String token, @RequestBody String id);

    @PostMapping(value = "api/user/getAllAdvertisementsByCategory")
    ResponseEntity<List<AdvertisementDTO>> getAdvertisements(@RequestHeader(name = "Authorization") String token, @RequestBody String id);

    @PostMapping(value = "api/images/addImage")
    ResponseEntity<String> addImage(@RequestHeader(name = "Authorization") String token, @RequestBody AdvertisementImage advertisementImage);

    @PostMapping(value = "api/user/addAdvertisement")
    ResponseEntity<String> addAdvertisement(@RequestHeader(name = "Authorization") String token, @RequestBody AdvertisementDTO advertisementDTO);

    @PostMapping(value = "api/user/getAdvertisement")
    ResponseEntity<AdvertisementDTO> getAdvertisement(@RequestHeader(name = "Authorization") String token, @RequestBody String id);
}

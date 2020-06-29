package com.netcracker.service;

import com.netcracker.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
@FeignClient("resource")
public interface FeignUserService {

    @PostMapping(value = "api/auth/login")
    ResponseEntity<String> login(@RequestBody LoginForm loginForm);

    @PostMapping(value = "api/auth/register")
    ResponseEntity<String> registration(@RequestBody UserRegisterDTO userRegisterDTO);

    @GetMapping(value = "api/auth/info")
    ResponseEntity<UserDTO> getUserInfo(@RequestHeader(name = "Authentication") String token);

    @PostMapping(value = "api/user/getFirstLayerCategories")
    ResponseEntity<List<CategoryDTO>> getFirstLayerCategories(@RequestHeader(name = "Authentication") String token, @RequestBody String id);

    @PostMapping(value = "api/user/getFirstLayerCategories")
    ResponseEntity<List<CategoryDTO>> getCategories(@RequestHeader(name = "Authentication") String token, @RequestBody String id);

    @PostMapping(value = "api/user/getAllAdvertisementsByCategory")
    ResponseEntity<List<AdvertisementDTO>> getAdvertisements(@RequestHeader(name = "Authentication") String token, @RequestBody CustomPair pair);

    @PostMapping(value = "api/images/addImage")
    ResponseEntity<String> addImage(@RequestHeader(name = "Authentication") String token, @RequestBody AdvertisementImage advertisementImage);

    @PostMapping(value = "api/user/addAdvertisement")
    ResponseEntity<String> addAdvertisement(@RequestHeader(name = "Authentication") String token, @RequestBody AdvertisementDTO advertisementDTO);

    @PostMapping(value = "api/user/getAdvertisement")
    ResponseEntity<AdvertisementDTO> getAdvertisement(@RequestHeader(name = "Authentication") String token, @RequestBody String id);

    @PostMapping(value = "api/user/getAllAdvertisementsBySearch")
    ResponseEntity<List<AdvertisementDTO>> getAdvertisementsBySearch(@RequestHeader(name = "Authentication") String token,
                                                                     @RequestBody MainPageParams params);

    @PostMapping(value = "api/user/getCategoryList")
    ResponseEntity<List<Long>> getCategoryList(@RequestHeader(name = "Authentication") String token, @RequestBody String id);

    @PostMapping(value = "api/admin/addCategory")
    ResponseEntity<String> addCategory(@RequestHeader(name = "Authentication") String token, @RequestBody CategoryDTO categoryDTO);

    @DeleteMapping(value = "api/admin/deleteCategory")
    ResponseEntity<String> deleteCategory(@RequestHeader(name = "Authentication") String token, @RequestBody String id);

    @DeleteMapping(value = "api/user/deleteAdvertisement")
    ResponseEntity<List<Long>> deleteAdvertisement(@RequestHeader(name = "Authentication") String token, @RequestBody String id);

    @PutMapping(value = "api/user/updateAdvertisement")
    ResponseEntity<String> updateAdvertisement(@RequestHeader(name = "Authentication") String token, @RequestBody AdvertisementDTO advertisementDTO);

    //TODO admin or user service for convenience
    @GetMapping(value = "api/admin/roleCheck")
    ResponseEntity<String> roleCheck(@RequestHeader(name = "Authentication") String token);

    @DeleteMapping(value = "api/admin/deleteAdvertisement")
    ResponseEntity<List<Long>> adminDeleteAdvertisement(@RequestHeader(name = "Authentication") String token, @RequestBody String id);

    @PutMapping(value = "api/admin/updateAdvertisement")
    ResponseEntity<String> adminUpdateAdvertisement(@RequestHeader(name = "Authentication") String token, @RequestBody AdvertisementDTO advertisementDTO);

    @PostMapping(value = "api/user/getAdvertisementsCount")
    ResponseEntity<Integer> getCount(@RequestHeader(name = "Authentication") String token,
                                     @RequestBody MainPageParams params);

    @PostMapping(value = "api/user/findById")
    ResponseEntity<UserDTO> getUserById(@RequestHeader(name = "Authentication") String token, @RequestBody String id);

    @DeleteMapping(value = "api/admin/deleteUser")
    ResponseEntity<String> deleteUser(@RequestHeader(name = "Authentication") String token, @RequestBody String id);

    @PutMapping(value = "api/admin/changePassword")
    ResponseEntity<String> changeUserPassword(@RequestHeader(name = "Authentication") String token, @RequestBody CustomPair pair);

    @PutMapping(value = "api/admin/changeStatus")
    ResponseEntity<String> changeUserStatus(@RequestHeader(name = "Authentication") String token, @RequestBody CustomPair pair);


    @PostMapping(value = "api/user/getAdvertisementsByUser")
    ResponseEntity<List<AdvertisementDTO>> getUsersAdvertisements(@RequestHeader(name = "Authentication") String token,
                                                                  @RequestBody String id);

    @GetMapping(value = "api/user/getUserRooms")
    ResponseEntity<List<RoomDTO>> getUserRooms(@RequestHeader(name = "Authentication") String token);

    @PostMapping(value = "api/user/getRoomMessages")
    ResponseEntity<List<MessageDTO>> getRoomMessages(@RequestHeader(name = "Authentication") String token,
                                                     @RequestBody String id);

    @PostMapping(value = "api/user/receiveMessage")
    ResponseEntity<MessageDTO> receiveMessage(@RequestHeader(name = "Authentication") String token,
                                              @RequestBody CustomPair pair);

    @GetMapping(value = "api/user/hasUnreadMessages")
    ResponseEntity<Boolean> hasUnreadMessages(@RequestHeader(name = "Authentication") String token);

    @PostMapping(value = "api/user/getRoomById")
    ResponseEntity<RoomDTO> getRoomById(@RequestHeader(name = "Authentication") String token, @RequestBody String id);

    @PostMapping(value = "api/user/setRead")
    ResponseEntity<String> setMessageRead(@RequestHeader(name = "Authentication") String token, @RequestBody String id);

}

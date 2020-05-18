package com.netcracker.service;

import com.netcracker.dto.LoginForm;
import com.netcracker.dto.UserDTO;
import com.netcracker.dto.UserRegisterDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@FeignClient("resource")
public interface FeignUserService {

    @PostMapping (value = "api/auth/login")
    ResponseEntity<String> login(@RequestBody LoginForm loginForm);

    @PostMapping(value = "api/auth/register")
    ResponseEntity<String> registration(@RequestBody UserRegisterDTO userRegisterDTO);

    @GetMapping(value = "api/auth/info" )
    ResponseEntity<UserDTO> getUserInfo(@RequestHeader(name = "Authorization") String token);
}

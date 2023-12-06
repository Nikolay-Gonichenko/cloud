package ru.itmo.cloud.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.cloud.model.input.UserDtoForLogin;
import ru.itmo.cloud.service.business.UserService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/cloud")
public class LoginController {

    private final UserService userService;

    @PostMapping("login")
    public ResponseEntity<UUID> login(@RequestBody UserDtoForLogin userDtoForLogin) {
        return ResponseEntity.ok(userService.login(userDtoForLogin));
    }

    @PostMapping("register")
    public ResponseEntity<UUID> register(@RequestBody UserDtoForLogin userDtoForLogin) {
        return ResponseEntity.ok(userService.register(userDtoForLogin));
    }
}

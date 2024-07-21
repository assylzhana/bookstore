package com.micrservices.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/us")
public class UserController {

    @GetMapping
    public ResponseEntity<String> demo(){
        return ResponseEntity.ok("hi");
    }
}

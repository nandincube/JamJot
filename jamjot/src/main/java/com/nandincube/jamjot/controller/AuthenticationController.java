package com.nandincube.jamjot.controller;

import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {


    @PostMapping
    public RequestEntity<String> signup(){

        return null;
    }

    @PostMapping
    public RequestEntity<String> login(){

        return null;
    }
}

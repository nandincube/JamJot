package com.nandincube.jamjot.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    public AuthenticationController(){
        
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(){

        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(){
        
        return null;
    }
}

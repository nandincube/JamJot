package com.nandincube.jamjot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nandincube.jamjot.model.User;
import com.nandincube.jamjot.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }
    

    // @GetMapping("/home")
    // public ResponseEntity<String> home(){

    //     return ResponseEntity.ok("Welcome Home!");
    // }

    
    // @GetMapping("/secured")
    // public ResponseEntity<String> secured(){

    //     return ResponseEntity.ok("Logged In!");
    // }


    // @PostMapping("/signup")
    // public ResponseEntity<String> signup(){

    //     return null;
    // }

    // @PostMapping("/login")
    // public ResponseEntity<String> login(){
        
    //     return null;
    // }
    @GetMapping("/secured")
    public ResponseEntity<Void> login(){

         return ResponseEntity.ok().build();
    }


    // @GetMapping("/dummy-login")
    // public ResponseEntity<User> loginDummy(){
    //     User user = authenticationService.createUserIfNotExists("dummy-id", "Dummy User");
    //     return ResponseEntity.ok().body(user);
    // }
}

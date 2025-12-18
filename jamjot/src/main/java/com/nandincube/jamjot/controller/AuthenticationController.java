package com.nandincube.jamjot.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    public AuthenticationController(){
        
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
}

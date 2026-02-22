package com.nandincube.jamjot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Jamjot API",
        version = "1.0.0",
        description = "A Spotify playlist annotation API that allows users to add notes/annotations to their Spotify playlists and tracks."
    )
)
public class JamjotApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(JamjotApplication.class, args);
	}

}

package com.nandincube.jamjot.controller;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.nandincube.jamjot.service.AnnotationService;
import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.exceptions.TrackNotFoundException;
import com.nandincube.jamjot.dto.TrackDTO;
import com.nandincube.jamjot.dto.PlaylistDTO;

@RestController
@RequestMapping("/annotations")
public class AnnotationsController {

    private static AnnotationService annotationService;

    public AnnotationsController(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    @GetMapping("/")
    public String dashboard() {
        return "Annotations Dashboard";
    }

    /**
     * 
     * @return
     */
    @GetMapping("/playlists")
    public ResponseEntity<ArrayList<PlaylistDTO>> getPlaylists() {
        ArrayList<PlaylistDTO> playlists = annotationService.getPlaylists();  
        playlists.forEach(playlist -> {
            System.out.println(playlist.name());
            System.out.println(playlist.id());
        }); 
        return ResponseEntity.ok(playlists);
    }

    /**
     * TODO:
     * 
     * @return
     */
    @GetMapping("/playlists/{playlistID}/note")
    public ResponseEntity<String> getPlaylistNote(Authentication userToken, @PathVariable String playlistID) {
        String userID = userToken.getName();
        try {
            return ResponseEntity.ok(annotationService.getPlaylistNote(userID, playlistID));
        } catch (PlaylistNotFoundException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * TODO:
     * 
     * @return
     */
    @PostMapping("/playlists/{playlistID}/note")
    public ResponseEntity<String> updatePlaylistNote(Authentication userToken, @PathVariable String playlistID,
            @RequestBody String note) {
        String userID = userToken.getName();

        try {
            annotationService.updatePlaylistNote(userID, playlistID, note);
            return ResponseEntity.ok("Playlist Note Updated!");
        } catch (PlaylistNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * TODO:
     * 
     * @return
     */
    @DeleteMapping("/playlists/{playlistID}/note")
    public ResponseEntity<String> deletePlaylistNote(Authentication userToken, @PathVariable String playlistID) {
        // return ResponseEntity.ok().build();
        String userID = userToken.getName();
        try {
            annotationService.deletePlaylistNote(userID, playlistID);
            return ResponseEntity.ok("Playlist Note Deleted!");
        } catch (PlaylistNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
   
     */
    @GetMapping("/playlists/{playlistID}/tracks")
    public ResponseEntity<ArrayList<TrackDTO>> getTracks(@PathVariable String playlistID) {
        ArrayList<TrackDTO> tracks = annotationService.getTracks(playlistID);
        return ResponseEntity.ok(tracks);
    }

    /**
     * TODO:
     * 
     * @return
     */
    @GetMapping("/playlists/{playlistID}/track/{trackID}/note")
    public ResponseEntity<String> getTrackNote(Authentication userToken, @PathVariable String playlistID,
            @PathVariable String trackID,
            @RequestParam(required = true) Integer trackNumber) {
        String userID = userToken.getName();

        try {
            return ResponseEntity.ok(annotationService.getTrackNote(userID, playlistID, trackID, trackNumber));
        } catch (PlaylistNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (TrackNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * TODO:
     * 
     * @return
     */
    @PostMapping("/playlists/{playlistID}/track/{trackID}/note")
    public ResponseEntity<String> updateTrackNote(Authentication userToken, @PathVariable String playlistID,
            @PathVariable String trackID,
            @RequestParam(required = true) Integer trackNumber, @RequestBody String note) {

        String userID = userToken.getName();

        try {
            annotationService.updateTrackNote(userID, playlistID, trackID, trackNumber, note);
            return ResponseEntity.ok("Track Note Updated!");
        } catch (PlaylistNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (TrackNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * TODO:
     * 
     * @return
     */
    @DeleteMapping("/playlists/{playlistID}/track/{trackID}/note")
    public ResponseEntity<String> deleteTrackNote(Authentication userToken, @PathVariable String playlistID,
            @PathVariable String trackID,
            @RequestParam(required = true) Integer trackNumber) {
        String userID = userToken.getName();

        try {
            annotationService.deleteTrackNote(userID, playlistID, trackID, trackNumber);
            return ResponseEntity.ok("Track Note Deleted!");
        } catch (PlaylistNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (TrackNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

}

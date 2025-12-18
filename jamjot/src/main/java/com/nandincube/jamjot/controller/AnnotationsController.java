package com.nandincube.jamjot.Controller;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nandincube.jamjot.Exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.Exceptions.TrackNotFoundException;
import com.nandincube.jamjot.Model.PlaylistMember;
import com.nandincube.jamjot.Service.AnnotationService;

@RestController
@RequestMapping("annotations/playlists")
public class AnnotationsController {

    private static AnnotationService annotationService;

    public AnnotationsController(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    /**
     * TODO:
     * 
     * 
     * ///GET users/{user_id}/playlists
        /// 
        //response.items.forEach(playlist -> {
        //    System.out.println(playlist.name);
        //    System.out.println(playlist.id)});
     * @return
     */
    @GetMapping("/")
    public ResponseEntity<Void> getPlaylists() {


        

        return ResponseEntity.ok().build();
    }

    /**
     * TODO:
     * 
     * @return
     */
    @GetMapping("/{playlistID}/note")
    public ResponseEntity<String> getPlaylistNote(Authentication user, @PathVariable String playlistID) {
        String userID = user.getName();
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
    @PostMapping("/{playlistID}/note")
    public ResponseEntity<Void> updatePlaylistNote(@PathVariable String playlistID) {
        return ResponseEntity.ok().build();
    }

    /**
     * TODO:
     * 
     * @return
     */
    @DeleteMapping("/{playlistID}/note")
    public ResponseEntity<Void> deletePlaylistNote(@PathVariable String playlistID) {
        return ResponseEntity.ok().build();
    }

    /**
     * TODO:
     * 
     *  ///GET /playlists/{playlist_id}/tracks
        /// 
        //res = response.items
        // for (int i =0 ; i< res.length; i++){
        //      track = res[i].track
        //    System.out.println(track.trackObject.name);
        //    System.out.println(track.trackObject.id)})
        // };
     * 
     * 
     * 
     * @return
     */
    @GetMapping("/{playlistID}/tracks")
    public ResponseEntity<Void> getTracks() {
        return ResponseEntity.ok().build();
    }

    /**
     * TODO:
     * 
     * @return
     */
    @GetMapping("/{playlistID}/track/{trackID}/note")
    public ResponseEntity<String> getTrackNote(Authentication user,@PathVariable String playlistID, @PathVariable String trackID,
            @RequestParam(required = true) Integer trackNumber) {
        String userID = user.getName();

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
    @PostMapping("/{playlistID}/track/{trackID}/note")
    public ResponseEntity<String> updateTrackNote(Authentication user, @PathVariable String playlistID, @PathVariable String trackID,
            @RequestParam(required = true) Integer trackNumber, @RequestBody String note) {

             String userID = user.getName();

        try {
            annotationService.updateTrackNote(userID, playlistID, trackID, trackNumber, note);
            return ResponseEntity.ok("Note Updated!");
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
    @DeleteMapping("/{playlistID}/track/{trackID}/note")
    public ResponseEntity<String> deleteTrackNote(Authentication user, @PathVariable String playlistID, @PathVariable String trackID,
            @RequestParam(required = true) Integer trackNumber) {
          String userID = user.getName();

        try {
            annotationService.deleteTrackNote(userID, playlistID, trackID, trackNumber);
            return ResponseEntity.ok("Note Deleted!");
        } catch (PlaylistNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (TrackNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

}

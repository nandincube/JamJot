package com.nandincube.jamjot.controller;

import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.exceptions.TrackNotFoundException;
import com.nandincube.jamjot.exceptions.UserNotFoundException;
import com.nandincube.jamjot.service.TrackAnnotationService;
import com.nandincube.jamjot.dto.GenericResponse;
import com.nandincube.jamjot.dto.NoteDTO;
import com.nandincube.jamjot.dto.TrackDTO;

@RestController
@RequestMapping("/annotations")
@Tag(name = "Track Annotations", description = "Endpoints for managing track-level annotations")
public class TrackAnnotationsController {

        private final TrackAnnotationService trackAnnotationService;

        public TrackAnnotationsController(TrackAnnotationService trackAnnotationService) {
                this.trackAnnotationService = trackAnnotationService;
        }

        /**
         * This method retrieves all tracks in a specific playlist from Spotify using
         * the playlist ID.
         * 
         * @param playlistID - Spotify ID of the playlist.
         * @return
         */
        @Operation(summary = "Get Tracks in Playlist", description = "Retrieve all tracks in a specific playlist from Spotify")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OK - Tracks retrieved successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = TrackDTO.class)) }),
                        @ApiResponse(responseCode = "404", description = "Not Found - Playlist not found", content = {
                                        @Content(mediaType = "*/*") }),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - User authentication failed or user not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find user or issue with user authentication (re-authentication required)!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") })
        })
        @GetMapping("/playlists/{playlistID}/tracks")
        public ResponseEntity<?> getTracks(
                        @Parameter(description = "The Spotify ID for the specified playlist", required = true) @PathVariable String playlistID) {

                try {
                        ArrayList<TrackDTO> tracks = trackAnnotationService
                                        .getPlaylistTracksInfoFromSpotify(playlistID);
                        return ResponseEntity.ok(tracks);
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GenericResponse(e.getMessage()));
                } catch (RuntimeException e) {
                        if (e.getCause() instanceof PlaylistNotFoundException ex) { // if playlist is not found - i.e.
                                                                                    // playlist ID invalid
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new GenericResponse(ex.getMessage()));
                        }
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }

        }

        /**
         * This method retrieves the note for a specific track in a playlist for the
         * authenticated user.
         * 
         * @param userToken
         * @param playlistID
         * @param trackID
         * @param trackNumber
         * @return
         */
        @Operation(summary = "Get Track Note", description = "Retrieve the note for a specific track in a playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "Not Found - Track or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find playlist!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - User authentication failed or user not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find user or issue with user authentication (re-authentication required)!"}
                                                        """)) }),

                        @ApiResponse(responseCode = "200", description = "OK - Track note retrieved successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = NoteDTO.class), examples = @ExampleObject(value = """
                                                             {"note": "Sample track note"}
                                                        """))

                        }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") }) })
        @GetMapping("/playlists/{playlistID}/track/{trackID}/note")
        public ResponseEntity<?> getTrackNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for the specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number in playlist", required = true) @RequestParam(required = true) Integer trackNumber) {
                String userID = userToken.getName();

                try {
                        String note = trackAnnotationService.getTrackNote(userID, playlistID, trackID, trackNumber);
                        return ResponseEntity
                                        .ok(new NoteDTO(note));
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (TrackNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new GenericResponse(e.getMessage()));

                } catch (RuntimeException e) {
                        if (e.getCause() instanceof TrackNotFoundException ex) { // if playlist is not found - i.e.
                                                                                 // playlist ID invalid
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new GenericResponse(ex.getMessage()));
                        }

                        if (e.getCause() instanceof PlaylistNotFoundException ex) { // if playlist is not found - i.e.
                                // playlist ID invalid
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new GenericResponse(ex.getMessage()));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

        /**
         * This method updates the note for a specific track in a playlist for the
         * authenticated user.
         * 
         * @param userToken
         * @param playlistID
         * @param trackID
         * @param trackNumber
         * @param note
         * @return
         */
        @Operation(summary = "Edit Track Note", description = "Add or Update the note for a specific track in a playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "Not Found - Track or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find track or track number mismatch!"}
                                                        """))
                        }),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - User authentication failed or user not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find user or issue with user authentication (re-authentication required)!"}
                                                        """)) }),

                        @ApiResponse(responseCode = "200", description = "OK - Track note updated successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Track Note Updated!"}
                                                        """))
                        }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") }) })
        @PutMapping("/playlists/{playlistID}/tracks/{trackID}/note")
        public ResponseEntity<GenericResponse> editTrackNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number in playlist", required = true) @RequestParam(required = true) Integer trackNumber,
                        @RequestBody NoteDTO note) {

                String userID = userToken.getName();

                try {
                        trackAnnotationService.editTrackNote(userID, playlistID, trackID, trackNumber, note.getNote());
                        return ResponseEntity.ok(new GenericResponse("Track Note Updated!"));
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (TrackNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (RuntimeException e) {
                        if (e.getCause() instanceof PlaylistNotFoundException ep
                                        || e.getCause() instanceof TrackNotFoundException et) { // if playlist or track
                                                                                                // is not found - i.e.
                                // playlist or track ID invalid
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new GenericResponse(e.getMessage()));
                        }
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                }

                catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

        /**
         * This method deletes the note for a specific track in a playlist for the
         * authenticated user.
         * 
         * @param userToken
         * @param playlistID
         * @param trackID
         * @param trackNumber
         * @return
         */
        @Operation(summary = "Delete Track Note", description = "Delete the note for a specific track in a playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "Not Found - Track or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find track or track number mismatch!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "200", description = "OK - Track note deleted successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Track Note Deleted!"}
                                                        """))
                        }),
                          @ApiResponse(responseCode = "401", description = "Unauthorized - User authentication failed or user not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find user or issue with user authentication (re-authentication required)!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") }) })
        @DeleteMapping("/playlists/{playlistID}/tracks/{trackID}/note")
        public ResponseEntity<GenericResponse> deleteTrackNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number in playlist", required = true) @RequestParam(required = true) Integer trackNumber) {
                String userID = userToken.getName();

                try {
                        trackAnnotationService.deleteTrackNote(userID, playlistID, trackID, trackNumber);
                        return ResponseEntity.ok(new GenericResponse("Track Note Deleted!"));
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (TrackNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (RuntimeException e) {
                        if (e.getCause() instanceof PlaylistNotFoundException ep
                                        || e.getCause() instanceof TrackNotFoundException et) { // if playlist or track
                                                                                                // is not found - i.e.
                                // playlist or track ID invalid
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new GenericResponse(e.getMessage()));
                        }
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                }catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

}
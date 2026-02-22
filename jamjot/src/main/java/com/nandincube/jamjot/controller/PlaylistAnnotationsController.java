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
import com.nandincube.jamjot.exceptions.UserNotFoundException;
import com.nandincube.jamjot.service.PlaylistAnnotationService;
import com.nandincube.jamjot.dto.GenericResponse;
import com.nandincube.jamjot.dto.NoteDTO;
import com.nandincube.jamjot.dto.PlaylistDTO;

@RestController
@RequestMapping("/annotations/playlists")
@Tag(name = "Playlist Annotations", description = "Endpoints for managing playlist-level annotations")
public class PlaylistAnnotationsController {

        private final PlaylistAnnotationService playlistAnnotationService;

        public PlaylistAnnotationsController(PlaylistAnnotationService playlistAnnotationService) {
                this.playlistAnnotationService = playlistAnnotationService;
        }

        /**
         * This endpoint retrieves all playlists for the authenticated user.
         * This is retrieved via the Spotify API using the user's access token.
         * 
         * @return ResponseEntity<ArrayList<PlaylistDTO>> - List of playlists and their
         *         details, including name, description and spotify ID.
         */
        @Operation(summary = "Get Playlists", description = "Retrieve all playlists made by the authenticated user from Spotify")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Playlists retrieved successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = PlaylistDTO.class)) }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") })
        })
        @GetMapping("")
        public ResponseEntity<ArrayList<PlaylistDTO>> getPlaylists() {
                try {
                        ArrayList<PlaylistDTO> playlists = playlistAnnotationService.getPlaylistsInfoFromSpotify();
                        return ResponseEntity.ok(playlists);

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }

        }

        /**
         * This method retrieves the note for a specific playlist for the authenticated
         * user.
         * 
         * @param userToken  - Authentication token of the user.
         * @param playlistID - Spotify ID of the playlist.
         * @return ResponseEntity<GenericResponse> - Note associated with the playlist
         *         or error message if playlist is not found.
         */
        @Operation(summary = "Get Playlist Note", description = "Retrieve the note for a specific playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "Playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find playlist!"}
                                                        """)) }),

                        @ApiResponse(responseCode = "200", description = "Playlist note retrieved successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = NoteDTO.class), examples = @ExampleObject(value = """
                                                             {"note": "Sample playlist note"}
                                                        """)) }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") })
        })
        @GetMapping("/{playlistID}/note")
        public ResponseEntity<?> getPlaylistNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for the specified playlist", required = true) @PathVariable String playlistID) {
                String userID = userToken.getName();
                try {
                        return ResponseEntity.ok(new NoteDTO(
                                        playlistAnnotationService.getPlaylistNote(userID, playlistID)));
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (RuntimeException e) {
                        if (e.getCause() instanceof PlaylistNotFoundException ex) { // if playlist is not found - i.e.
                                                                                    // playlist ID invalid
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new GenericResponse(e.getMessage()));
                        }
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

        /**
         * This method updates the note for a specific playlist for the authenticated
         * user.
         * 
         * @param userToken  - Authentication token of the user.
         * @param playlistID - Spotify ID of the playlist.
         * @param note       - New note to be associated with the playlist.
         * @return ResponseEntity<GenericResponse> - Confirmation message upon
         *         successful update or error message if playlist is not found.
         */
        @Operation(summary = "Edit Playlist Note", description = "Add or Update the note for a specific playlist ")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find playlist!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "200", description = "Playlist note updated successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Playlist Note Updated!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") })
        })
        @PutMapping("/{playlistID}/note")
        public ResponseEntity<GenericResponse> editPlaylistNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for the specified playlist", required = true) @PathVariable String playlistID,
                        @RequestBody NoteDTO note) {
                String userID = userToken.getName();

                try {
                        playlistAnnotationService.editPlaylistNote(userID, playlistID, note.getNote());
                        return ResponseEntity.ok(new GenericResponse("Playlist Note Updated!"));
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (RuntimeException e) {
                        if (e.getCause() instanceof PlaylistNotFoundException ex) { // if playlist is not found
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new GenericResponse(e.getMessage()));
                        }
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

        /**
         * This method deletes the note for a specific playlist for the authenticated
         * user.
         * 
         * @param userToken  - Authentication token of the user.
         * @param playlistID - Spotify ID of the playlist.
         * @return ResponseEntity<GenericResponse> - Confirmation message upon
         *         successful or error message if playlist or user not found.
         *         deletion.
         */
        @Operation(summary = "Delete Playlist Note", description = "Delete the note for a specific playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find playlist!"}
                                                        """)) }),

                        @ApiResponse(responseCode = "200", description = "Playlist note deleted successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Playlist Note Deleted!"}
                                                        """))
                        }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") })
        })
        @DeleteMapping("/{playlistID}/note")
        public ResponseEntity<GenericResponse> deletePlaylistNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for the specified playlist", required = true) @PathVariable String playlistID) {

                String userID = userToken.getName();
                try {
                        playlistAnnotationService.deletePlaylistNote(userID, playlistID);
                        return ResponseEntity.ok(new GenericResponse("Playlist Note Deleted!"));
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (RuntimeException e) {
                        if (e.getCause() instanceof PlaylistNotFoundException ex) { // if playlist is not found - i.e.
                                                                                    // playlist ID invalid
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new GenericResponse(e.getMessage()));
                        }
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }

        }

}
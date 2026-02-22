package com.nandincube.jamjot.controller;

import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.nandincube.jamjot.exceptions.TimestampNotFoundException;
import com.nandincube.jamjot.exceptions.TrackNotFoundException;
import com.nandincube.jamjot.exceptions.UserNotFoundException;
import com.nandincube.jamjot.service.TimestampAnnotationService;
import com.nandincube.jamjot.dto.GenericResponse;
import com.nandincube.jamjot.dto.NoteDTO;
import com.nandincube.jamjot.dto.TimestampRequestDTO;
import com.nandincube.jamjot.dto.GetTimestampsResponse;

@RestController
@RequestMapping("/annotations/timestamps")
@Tag(name = "Annotations API", description = "Endpoints for managing playlist and track annotations")
public class TimestampAnnotationsController {

        private final TimestampAnnotationService timestampAnnotationService;

        public TimestampAnnotationsController(TimestampAnnotationService timestampAnnotationService) {
                this.timestampAnnotationService = timestampAnnotationService;
        }

        @Tag(name = "Edit", description = "Endpoints for adding or updating playlist and track notes")
        @Operation(summary = "Update Timestamp Note", description = "Update the note for a timestamp interval in a specific track that appears in a playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "Timestamp not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find timestamp with specified ID!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "200", description = "Timestamp note updated successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Timestamp Note Updated!"}
                                                        """))
                        }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") }) })

        @PutMapping("/{timestampID}/note")
        public ResponseEntity<GenericResponse> updateTimestampNote(Authentication userToken,
                        @Parameter(description = "The timestamp ID for the specified timestamp", required = true) @PathVariable Long timestampID,
                        @RequestBody NoteDTO note) {

                String userID = userToken.getName();

                try {
                        timestampAnnotationService.updateTimestampNote(userID, timestampID,
                                        note.getNote());
                        return ResponseEntity.ok(new GenericResponse("Timestamp note updated!"));
                } catch (TimestampNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

        @Tag(name = "Edit", description = "Endpoints for adding or updating playlist and track notes")
        @Operation(summary = "Add Timestamp Note", description = "Add a note for a timestamp interval in a specific track that appears in a playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User, track or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find track or track number mismatch!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "400", description = "Invalid timestamp interval - Invalid format, invalid start or end time, or end time exceeds track duration", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Time must be in the format mm:ss"}
                                                        """)) }),
                        @ApiResponse(responseCode = "200", description = "Timestamp note added successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Timestamp Note Added!"}
                                                        """))
                        }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") }) })

        @PostMapping("/playlists/{playlistID}/tracks/{trackID}/note")
        public ResponseEntity<GenericResponse> addTimestampNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number/position in playlist", required = true) @RequestParam(required = true) Integer trackNumber,
                        @RequestBody TimestampNoteRequestDTO note) {

                String userID = userToken.getName();

                try {
                        timestampAnnotationService.addTimestampNote(userID, playlistID, trackID, trackNumber,
                                        note.getNote(), intervalStart, intervalEnd);
                        return ResponseEntity.ok(new GenericResponse("Timestamp note added!"));
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (TrackNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

        @Tag(name = "Retrieval", description = "Endpoints for retrieving playlist and track information and notes")
        @Operation(summary = "Get Timestamp Notes", description = "Retrieve the timestamp notes for a specific track in a playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User, track or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find track or track number mismatch!"}
                                                        """)) }),
                           @ApiResponse(responseCode = "200", description = "Timestamp notes retrieved successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GetTimestampsResponse.class), examples = @ExampleObject(value = """
                                                             {
                                                                "items": [
                                                                    {
                                                                        "timestampID": 1,
                                                                        "start_time": "00:30",
                                                                        "end_time": "00:45",
                                                                        "note": "Sample timestamp note"
                                                                    },
                                                                    {
                                                                        "timestampID": 2,
                                                                        "start_time": "01:15",
                                                                        "end_time": "01:30",
                                                                        "note": "Another timestamp note",

                                                                    }
                                                                ]
                                                            }
                                                        """))
                        }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") }) })

        @GetMapping("/playlists/{playlistID}/tracks/{trackID}/note")
        public ResponseEntity<?> getTimestampNotes(Authentication userToken,
                        @Parameter(description = "The Spotify ID for specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number/position in playlist", required = true) @RequestParam(required = true) Integer trackNumber) {

                String userID = userToken.getName();

                try {
                        GetTimestampsResponse timestampResponse = timestampAnnotationService.getTimestampNotes(userID, playlistID, trackID, trackNumber);
                        return ResponseEntity.ok(timestampResponse);
                } catch (TrackNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

        @Tag(name = "Delete", description = "Endpoints for deleting playlist and track notes")
        @Operation(summary = "Delete Timestamp Note", description = "Delete the timestampnote for a specific track in a playlist")
        @ApiResponses(value = {
                         @ApiResponse(responseCode = "404", description = "Timestamp not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find timestamp with specified ID!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "200", description = "Timestamp note deleted successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Timestamp Note Deleted!"}
                                                        """))
                        }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") }) })
        @DeleteMapping("/{timestampID}/note")
        public ResponseEntity<GenericResponse> deleteTimestampNote(Authentication userToken,
                        @Parameter(description = "The timestamp ID for the specified timestamp", required = true) @PathVariable Long timestampID) {
                String userID = userToken.getName();

                try {
                        timestampAnnotationService.deleteTimestampNote(userID, timestampID);
                        return ResponseEntity.ok(new GenericResponse("Timestamp Note Deleted!"));
                } catch (TimestampNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

}

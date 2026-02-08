package com.nandincube.jamjot.controller;

import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import com.nandincube.jamjot.service.PlaylistAnnotationService;
import com.nandincube.jamjot.service.TrackAnnotationService;
import com.nandincube.jamjot.dto.GenericResponse;
import com.nandincube.jamjot.dto.NoteDTO;
import com.nandincube.jamjot.dto.PlaylistDTO;
import com.nandincube.jamjot.dto.TrackDTO;

@RestController
@RequestMapping("/annotations")
@Tag(name = "Annotations API", description = "Endpoints for managing playlist and track annotations")
public class AnnotationsController {

        private final PlaylistAnnotationService playlistAnnotationService;
        private final TrackAnnotationService trackAnnotationService;

        public AnnotationsController(PlaylistAnnotationService playlistAnnotationService,
                        TrackAnnotationService trackAnnotationService) {
                this.playlistAnnotationService = playlistAnnotationService;
                this.trackAnnotationService = trackAnnotationService;
        }

        /**
         * This endpoint retrieves all playlists for the authenticated user.
         * This is retrieved via the Spotify API using the user's access token.
         * 
         * @return ResponseEntity<ArrayList<PlaylistDTO>> - List of playlists and their
         *         details, including name, description and spotify ID.
         */
        @Tag(name = "Retrieval", description = "Endpoints for retrieving playlist and track information and notes")
        @Operation(summary = "Get Playlists", description = "Retrieve all playlists made by the authenticated user from Spotify")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Playlists retrieved successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = PlaylistDTO.class)) }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") })
        })
        @GetMapping("/playlists")
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
        @Tag(name = "Retrieval", description = "Endpoints for retrieving playlist and track information and notes")
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
        @GetMapping("/playlists/{playlistID}/note")
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
                                                .body(new GenericResponse(ex.getMessage()));
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
        @Tag(name = "Edit", description = "Endpoints for adding or updating playlist and track notes")
        @Operation(summary = "Edit Playlist Note", description = "Add or Update the note for a specific playlist ")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find user!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "200", description = "Playlist note updated successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Playlist Note Updated!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") })
        })
        @PutMapping("/playlists/{playlistID}/note")
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
                                                .body(new GenericResponse(ex.getMessage()));
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
        @Tag(name = "Delete", description = "Endpoints for deleting playlist and track notes")
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
        @DeleteMapping("/playlists/{playlistID}/note")
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
                                                .body(new GenericResponse(ex.getMessage()));
                        }
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }

        }

        /**
         * This method retrieves all tracks in a specific playlist from Spotify using the playlist ID.
         * @param playlistID - Spotify ID of the playlist.
         * @return
         */
        @Tag(name = "Retrieval", description = "Endpoints for retrieving playlist and track information and notes")
        @Operation(summary = "Get Tracks in Playlist", description = "Retrieve all tracks in a specific playlist from Spotify")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Tracks retrieved successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = TrackDTO.class)) }),
                        @ApiResponse(responseCode = "404", description = "Playlist not found", content = {
                                        @Content(mediaType = "*/*") }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") })
        })
        @GetMapping("/playlists/{playlistID}/tracks")
        public ResponseEntity<ArrayList<TrackDTO>> getTracks(
                        @Parameter(description = "The Spotify ID for the specified playlist", required = true) @PathVariable String playlistID) {

                try {
                        ArrayList<TrackDTO> tracks = trackAnnotationService
                                        .getPlaylistTracksInfoFromSpotify(playlistID);
                        return ResponseEntity.ok(tracks);
                } catch (RuntimeException e) {
                        if (e.getCause() instanceof PlaylistNotFoundException ex) { // if playlist is not found - i.e.
                                                                                    // playlist ID invalid
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .build();
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
        @Tag(name = "Retrieval", description = "Endpoints for retrieving playlist and track information and notes")
        @Operation(summary = "Get Track Note", description = "Retrieve the note for a specific track in a playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "Track or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find playlist!"}
                                                        """)) }),

                        @ApiResponse(responseCode = "200", description = "Track note retrieved successfully", content = {
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
                } catch (RuntimeException e) {
                        if (e.getCause() instanceof TrackNotFoundException ex) { // if playlist is not found - i.e.
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
        @Tag(name = "Edit", description = "Endpoints for adding or updating playlist and track notes")
        @Operation(summary = "Edit Track Note", description = "Add or Update the note for a specific track in a playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User, track or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find track or track number mismatch!"}
                                                        """))
                        }),

                        @ApiResponse(responseCode = "200", description = "Track note updated successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Track Note Updated!"}
                                                        """))
                        }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") }) })
        @PutMapping("/playlists/{playlistID}/track/{trackID}/note")
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
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
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
        @Tag(name = "Delete", description = "Endpoints for deleting playlist and track notes")
        @Operation(summary = "Delete Track Note", description = "Delete the note for a specific track in a playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User, track or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Error: Could not find track or track number mismatch!"}
                                                        """)) }),
                        @ApiResponse(responseCode = "200", description = "Track note deleted successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = GenericResponse.class), examples = @ExampleObject(value = """
                                                             {"message": "Track Note Deleted!"}
                                                        """))
                        }),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                                        @Content(mediaType = "*/*") }) })
        @DeleteMapping("/playlists/{playlistID}/track/{trackID}/note")
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
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new GenericResponse(e.getMessage()));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

        // @Tag(name = "Edit", description = "Endpoints for adding or updating playlist
        // and track notes")
        // @Operation(summary = "Edit Timestamp Note", description = "Edit the note for
        // a specific timestamp in a track")
        // @ApiResponses(value = {
        // @ApiResponse(responseCode = "404", description = "User, track, playlist or
        // timestamp not found", content = {
        // @Content(mediaType = "*/*", schema = @Schema(example = "Error: Could not find
        // timestamp!")) }),

        // @ApiResponse(responseCode = "200", description = "Timestamp note updated
        // successfully", content = {
        // @Content(mediaType = "*/*", schema = @Schema(example = "Timestamp Note
        // Updated!"))
        // }) })
        // @PutMapping("/playlists/{playlistID}/track/{trackID}/timestamp/{timestampID}/note")
        // public ResponseEntity<String> updateTimestampNote(Authentication userToken,
        // @Parameter(description = "The Spotify ID for specified playlist", required =
        // true) @PathVariable String playlistID,
        // @Parameter(description = "The Spotify ID for the specified track", required =
        // true) @PathVariable String trackID,
        // @Parameter(description = "The track number in playlist", required = true)
        // @RequestParam(required = true) Integer trackNumber,
        // @Parameter(description = "The timestamp ID for the specified timestamp",
        // required = true) @PathVariable Long timestampID,
        // @RequestBody NoteRequest note) {

        // String userID = userToken.getName();

        // try {
        // annotationService.updateTimestampNote(userID, playlistID, trackID,
        // trackNumber, note.getNote(),
        // timestampID);
        // return ResponseEntity.ok("Timestamp Note Updated!");
        // } catch (PlaylistNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(e.getMessage());
        // } catch (TrackNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(e.getMessage());
        // } catch (UserNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(e.getMessage());
        // } catch (TimestampNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(e.getMessage());
        // }
        // }

        // @Tag(name = "Edit", description = "Endpoints for adding or updating playlist
        // and track notes")
        // @Operation(summary = "Add Timestamp Note", description = "Add the note for a
        // specific timestamp in a track")
        // @ApiResponses(value = {
        // @ApiResponse(responseCode = "404", description = "User, track or playlist not
        // found", content = {
        // @Content(mediaType = "*/*", schema = @Schema(example = "Error: Could not find
        // playlist!")) }),
        // @ApiResponse(responseCode = "400", description = "Start/End time format is
        // invalid or start and end time interval is invalid", content = {
        // @Content(mediaType = "*/*", schema = @Schema(example = "Start time must be in
        // the format mm:ss")) }),

        // @ApiResponse(responseCode = "200", description = "Timestamp note updated
        // successfully", content = {
        // @Content(mediaType = "*/*", schema = @Schema(example = "Timestamp Note
        // Updated!"))
        // }) })
        // @PostMapping("/playlists/{playlistID}/tracks/{trackID}/timestamps/note")
        // public ResponseEntity<String> addTimestampNote(Authentication userToken,
        // @Parameter(description = "The Spotify ID for specified playlist", required =
        // true) @PathVariable String playlistID,
        // @Parameter(description = "The Spotify ID for the specified track", required =
        // true) @PathVariable String trackID,
        // @Parameter(description = "The track number in playlist", required = true)
        // @RequestParam(required = true) Integer trackNumber,
        // @Parameter(description = "The start time in mins and seconds (mm:ss) for the
        // timestamp", required = true) @RequestParam(required = true) String
        // startInMins,
        // @Parameter(description = "The end time in mins and seconds (mm:ss) for the
        // timestamp", required = true) @RequestParam(required = true) String endInMins,
        // @RequestBody NoteRequest note) {

        // String userID = userToken.getName();

        // try {
        // annotationService.addTimestampNote(userID, playlistID, trackID, trackNumber,
        // startInMins,
        // endInMins, note.getNote());
        // return ResponseEntity.ok("Timestamp Note Added!");
        // } catch (PlaylistNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(e.getMessage());
        // } catch (TrackNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(e.getMessage());
        // } catch (UserNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(e.getMessage());
        // } catch (IllegalArgumentException e) {
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        // .body(e.getMessage());
        // }
        // }

        // @Tag(name = "Retrieval", description = "Endpoints for retrieving playlist and
        // track information and notes")
        // @Operation(summary = "Get Timestamps in Track", description = "Retrieve all
        // timestamps in a specific track in a playlist")
        // @ApiResponses(value = {
        // @ApiResponse(responseCode = "404", description = "User, track or playlist not
        // found",
        // content = {
        // @Content(mediaType = "*/*", schema = @Schema(example = "Error: Could not find
        // track or track number mismatch!")) }
        // ),
        // @ApiResponse(responseCode = "200", description = "Timestamps retrieved
        // successfully", content = {
        // @Content(mediaType = "*/*", schema = @Schema(implementation =
        // TimestampDTO.class))
        // }) })

        // @GetMapping("/playlists/{playlistID}/track/{trackID}/timestamps")
        // public ResponseEntity<TimestampResponse> getTimestampNotes(
        // Authentication userToken,
        // @Parameter(description = "The Spotify ID for specified playlist", required =
        // true) @PathVariable String playlistID,
        // @Parameter(description = "The Spotify ID for the specified track", required =
        // true) @PathVariable String trackID,
        // @Parameter(description = "The track number in playlist", required = true)
        // @RequestParam(required = true) Integer trackNumber) {
        // String userID = userToken.getName();
        // TimestampResponse timestamps = null;
        // try {
        // timestamps = annotationService.getTimestampNotes(userID, playlistID, trackID,
        // trackNumber);
        // return ResponseEntity.ok(timestamps);
        // } catch (PlaylistNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        // } catch (TrackNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        // }catch (UserNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        // }
        // }

        // @Tag(name = "Delete", description = "Endpoints for deleting playlist and
        // track notes")
        // @Operation(summary = "Delete Timestamp Note", description = "Delete the note
        // for a specific timestamp in a track")
        // @ApiResponses(value = {
        // @ApiResponse(responseCode = "404", description = "User, track, playlist or
        // timestamp not found", content = {
        // @Content(mediaType = "*/*", schema = @Schema(example = "Error: Could not find
        // timestamp!")) }),

        // @ApiResponse(responseCode = "200", description = "Timestamp note deleted
        // successfully", content = {
        // @Content(mediaType = "*/*", schema = @Schema(example = "Timestamp Note
        // Deleted!"))
        // }) })
        // @DeleteMapping("/playlists/{playlistID}/track/{trackID}/timestamp/{timestampID}/note")
        // public ResponseEntity<String> deleteTimestampNote(
        // Authentication userToken,
        // @Parameter(description = "The Spotify ID for specified playlist", required =
        // true) @PathVariable String playlistID,
        // @Parameter(description = "The Spotify ID for the specified track", required =
        // true) @PathVariable String trackID,
        // @Parameter(description = "The track number in playlist", required = true)
        // @RequestParam(required = true) Integer trackNumber,
        // @Parameter(description = "The timestamp ID for the specified timestamp",
        // required = true) @PathVariable Long timestampID) {

        // String userID = userToken.getName();

        // try {
        // annotationService.deleteTimestampNote(userID, playlistID, trackID,
        // trackNumber, timestampID);
        // return ResponseEntity.ok("Timestamp Note Deleted!");
        // } catch (PlaylistNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(e.getMessage());
        // } catch (TrackNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(e.getMessage());
        // } catch (UserNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(e.getMessage());
        // } catch (TimestampNotFoundException e) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(e.getMessage());

        // }
        // }

}

package com.nandincube.jamjot.controller;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

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

import com.nandincube.jamjot.service.AnnotationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.exceptions.TimestampNotFoundException;
import com.nandincube.jamjot.exceptions.TrackNotFoundException;
import com.nandincube.jamjot.exceptions.UserNotFoundException;
import com.nandincube.jamjot.dto.NoteRequest;
import com.nandincube.jamjot.dto.PlaylistDTO;
import com.nandincube.jamjot.dto.TimestampDTO;
import com.nandincube.jamjot.dto.TimestampResponse;
import com.nandincube.jamjot.dto.TrackDTO;

@RestController
@RequestMapping("/annotations")
@Tag(name = "Annotations API", description = "Endpoints for managing playlist and track annotations")
public class AnnotationsController {

        private final AnnotationService annotationService;

        public AnnotationsController(AnnotationService annotationService) {
                this.annotationService = annotationService;
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
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = PlaylistDTO.class)) }

                        )
        })
        @GetMapping("/playlists")
        public ResponseEntity<ArrayList<PlaylistDTO>> getPlaylists() {
                ArrayList<PlaylistDTO> playlists = annotationService.getPlaylistsFromSpotify();
                return ResponseEntity.ok(playlists);
        }

        /**
         * This method retrieves the note for a specific playlist for the authenticated
         * user.
         * 
         * @param userToken  - Authentication token of the user.
         * @param playlistID - Spotify ID of the playlist.
         * @return ResponseEntity<String> - Note associated with the playlist.
         */
        @Tag(name = "Retrieval", description = "Endpoints for retrieving playlist and track information and notes")
        @Operation(summary = "Get Playlist Note", description = "Retrieve the note for a specific playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "Playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Sorry :( Could not find playlist!")) }),

                        @ApiResponse(responseCode = "200", description = "Playlist note retrieved successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Sample playlist note")) })
        })
        @GetMapping("/playlists/{playlistID}/note")
        public ResponseEntity<String> getPlaylistNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for the specified playlist", required = true) @PathVariable String playlistID) {
                String userID = userToken.getName();
                try {
                        return ResponseEntity.ok(annotationService.getPlaylistNote(userID, playlistID));
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                }
        }

        /**
         * This method updates the note for a specific playlist for the authenticated
         * user.
         * 
         * @param userToken  - Authentication token of the user.
         * @param playlistID - Spotify ID of the playlist.
         * @param note       - New note to be associated with the playlist.
         * @return ResponseEntity<String> - Confirmation message upon successful update.
         */
        @Tag(name = "Edit", description = "Endpoints for adding or updating playlist and track notes")
        @Operation(summary = "Edit Playlist Note", description = "Add or Update the note for a specific playlist ")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Sorry :( Could not find user!")) }),

                        @ApiResponse(responseCode = "200", description = "Playlist note updated successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Playlist Note Updated!")) })
        })
        @PostMapping("/playlists/{playlistID}/note")
        public ResponseEntity<String> editPlaylistNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for the specified playlist", required = true) @PathVariable String playlistID,
                        @RequestBody NoteRequest note) {
                String userID = userToken.getName();

                try {
                        annotationService.updatePlaylistNote(userID, playlistID, note.getNote());
                        return ResponseEntity.ok("Playlist Note Updated!");
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                }
        }

        /**
         * This method deletes the note for a specific playlist for the authenticated
         * user.
         * 
         * @param userToken  - Authentication token of the user.
         * @param playlistID - Spotify ID of the playlist.
         * @return ResponseEntity<String> - Confirmation message upon successful
         *         deletion.
         */
        @Tag(name = "Delete", description = "Endpoints for deleting playlist and track notes")
        @Operation(summary = "Delete Playlist Note", description = "Delete the note for a specific playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Sorry :( Could not find playlist!")) }),

                        @ApiResponse(responseCode = "200", description = "Playlist note deleted successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Playlist Note Deleted!"))
                        })
        })
        @DeleteMapping("/playlists/{playlistID}/note")
        public ResponseEntity<String> deletePlaylistNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for the specified playlist", required = true) @PathVariable String playlistID) {
                // return ResponseEntity.ok().build();
                String userID = userToken.getName();
                try {
                        annotationService.deletePlaylistNote(userID, playlistID);
                        return ResponseEntity.ok("Playlist Note Deleted!");
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                }

        }

        /**
         * TODO: Done but need to add error handling in endpoint
         * 
         * @param playlistID
         * @return
         */
        @Tag(name = "Retrieval", description = "Endpoints for retrieving playlist and track information and notes")
        @Operation(summary = "Get Tracks in Playlist", description = "Retrieve all tracks in a specific playlist from Spotify")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Tracks retrieved successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = TrackDTO.class)) }),
        })
        @GetMapping("/playlists/{playlistID}/tracks")
        public ResponseEntity<ArrayList<TrackDTO>> getTracks(
                        @Parameter(description = "The Spotify ID for the specified playlist", required = true) @PathVariable String playlistID) {
                ArrayList<TrackDTO> tracks = annotationService.getTracksFromSpotify(playlistID);
                return ResponseEntity.ok(tracks);
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
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Sorry :( Could not find playlist!")) }),

                        @ApiResponse(responseCode = "200", description = "Track note retrieved successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Sample track note"))
                        }) })
        @GetMapping("/playlists/{playlistID}/track/{trackID}/note")
        public ResponseEntity<String> getTrackNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for the specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number in playlist", required = true) @RequestParam(required = true) Integer trackNumber) {
                String userID = userToken.getName();

                try {
                        return ResponseEntity
                                        .ok(annotationService.getTrackNote(userID, playlistID, trackID, trackNumber));
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (TrackNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
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
                                        @Content(mediaType = "*/*") }),

                        @ApiResponse(responseCode = "200", description = "Track note updated successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Track Note Updated!"))
                        }) })
        @PostMapping("/playlists/{playlistID}/track/{trackID}/note")
        public ResponseEntity<String> editTrackNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number in playlist", required = true) @RequestParam(required = true) Integer trackNumber,
                        @RequestBody NoteRequest note) {

                String userID = userToken.getName();

                try {
                        annotationService.editTrackNote(userID, playlistID, trackID, trackNumber, note.getNote());
                        return ResponseEntity.ok("Track Note Updated!");
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (TrackNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
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
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Sorry :( Could not find track or track number mismatch!")) }),

                        @ApiResponse(responseCode = "200", description = "Track note deleted successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Track Note Deleted!"))
                        }) })
        @DeleteMapping("/playlists/{playlistID}/track/{trackID}/note")
        public ResponseEntity<String> deleteTrackNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number in playlist", required = true) @RequestParam(required = true) Integer trackNumber) {
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
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                }
        }


        @Tag(name = "Edit", description = "Endpoints for adding or updating playlist and track notes")
        @Operation(summary = "Edit Timestamp Note", description = "Edit the note for a specific timestamp in a track")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User, track, playlist or timestamp not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Sorry :( Could not find timestamp!")) }),

                        @ApiResponse(responseCode = "200", description = "Timestamp note updated successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Timestamp Note Updated!"))
                        }) })
        @PutMapping("/playlists/{playlistID}/track/{trackID}/timestamp/{timestampID}/note")
        public ResponseEntity<String> updateTimestampNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number in playlist", required = true) @RequestParam(required = true) Integer trackNumber,
                        @Parameter(description = "The timestamp ID for the specified timestamp", required = true) @PathVariable Long timestampID,
                        @RequestBody NoteRequest note) {

                String userID = userToken.getName();

                try {
                        annotationService.updateTimestampNote(userID, playlistID, trackID, trackNumber, note.getNote(),
                                        timestampID);
                        return ResponseEntity.ok("Timestamp Note Updated!");
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (TrackNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (TimestampNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                }
        }

        @Tag(name = "Edit", description = "Endpoints for adding or updating playlist and track notes")
        @Operation(summary = "Add Timestamp Note", description = "Add the note for a specific timestamp in a track")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User, track or playlist not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Sorry :( Could not find playlist!")) }),
                        @ApiResponse(responseCode = "400", description = "Start/End time format is invalid or start and end time interval is invalid", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Start time must be in the format mm:ss")) }),

                        @ApiResponse(responseCode = "200", description = "Timestamp note updated successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Timestamp Note Updated!"))
                        }) })
        @PostMapping("/playlists/{playlistID}/tracks/{trackID}/timestamps/note")
        public ResponseEntity<String> addTimestampNote(Authentication userToken,
                        @Parameter(description = "The Spotify ID for specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number in playlist", required = true) @RequestParam(required = true) Integer trackNumber,
                        @Parameter(description = "The start time in mins and seconds (mm:ss) for the timestamp", required = true) @RequestParam(required = true) String startInMins,
                        @Parameter(description = "The end time in mins and seconds (mm:ss) for the timestamp", required = true) @RequestParam(required = true) String endInMins,
                        @RequestBody NoteRequest note) {

                String userID = userToken.getName();

                try {
                        annotationService.addTimestampNote(userID, playlistID, trackID, trackNumber, startInMins,
                                        endInMins, note.getNote());
                        return ResponseEntity.ok("Timestamp Note Added!");
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (TrackNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(e.getMessage());
                }
        }

        @Tag(name = "Retrieval", description = "Endpoints for retrieving playlist and track information and notes")
        @Operation(summary = "Get Timestamps in Track", description = "Retrieve all timestamps in a specific track in a playlist")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User, track or playlist not found",
                                content = {
                                                @Content(mediaType = "*/*", schema = @Schema(example = "Sorry :( Could not find track or track number mismatch!")) }
                        ),
                        @ApiResponse(responseCode = "200", description = "Timestamps retrieved successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(implementation = TimestampDTO.class))
                        }) })

        @GetMapping("/playlists/{playlistID}/track/{trackID}/timestamps")
        public ResponseEntity<TimestampResponse> getTimestampNotes(
                        Authentication userToken,
                        @Parameter(description = "The Spotify ID for specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number in playlist", required = true) @RequestParam(required = true) Integer trackNumber) {
                String userID = userToken.getName();
                TimestampResponse timestamps = null;
                try {
                        timestamps =  annotationService.getTimestampNotes(userID, playlistID, trackID, trackNumber);
                        return ResponseEntity.ok(timestamps);
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                } catch (TrackNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
        }


        @Tag(name = "Delete", description = "Endpoints for deleting playlist and track notes")
        @Operation(summary = "Delete Timestamp Note", description = "Delete the note for a specific timestamp in a track")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "404", description = "User, track, playlist or timestamp not found", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Sorry :( Could not find timestamp!")) }),

                        @ApiResponse(responseCode = "200", description = "Timestamp note deleted successfully", content = {
                                        @Content(mediaType = "*/*", schema = @Schema(example = "Timestamp Note Deleted!"))
                        }) })
        @DeleteMapping("/playlists/{playlistID}/track/{trackID}/timestamp/{timestampID}/note")
        public ResponseEntity<String> deleteTimestampNote(
                        Authentication userToken,
                        @Parameter(description = "The Spotify ID for specified playlist", required = true) @PathVariable String playlistID,
                        @Parameter(description = "The Spotify ID for the specified track", required = true) @PathVariable String trackID,
                        @Parameter(description = "The track number in playlist", required = true) @RequestParam(required = true) Integer trackNumber,
                        @Parameter(description = "The timestamp ID for the specified timestamp", required = true) @PathVariable Long timestampID) {

                String userID = userToken.getName();

                try {
                        annotationService.deleteTimestampNote(userID, playlistID, trackID, trackNumber, timestampID);
                        return ResponseEntity.ok("Timestamp Note Deleted!");
                } catch (PlaylistNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (TrackNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (UserNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());
                } catch (TimestampNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(e.getMessage());

                }
        }

}

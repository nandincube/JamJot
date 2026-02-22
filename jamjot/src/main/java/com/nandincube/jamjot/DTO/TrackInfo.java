package com.nandincube.jamjot.dto;

import java.time.Duration;
import java.util.ArrayList;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for encapsulating the details of a Spotify track, including its ID, name,
 * artists, album information, and duration.
 * This DTO is used for general track information retrieval, such as when
 * searching for tracks or retrieving track details outside the context of a
 * playlist.
 *
 * This DTO is meant to mirror the structure of the TrackObject found in the
 * Spotify API response for retrieving tracks, which includes all relevant
 * metadata about a track.
 */
public class TrackInfo {

        @Schema(example = "5FGhfmC9fl7TQ23pybMUyp", description = "The Spotify ID of the track")
        private String id;
        @Schema(example = "Life Will Be", description = "The name of the track")
        private String name;
        @Schema(description = "The album information of the track")
        private Album album;
        @Schema(description = "The list of artists for the track")
        private ArrayList<Artist> artists;
        @Schema(example = "180000", description = "The duration of the track in milliseconds")
        private Integer duration_ms;
        @Schema(example = "03:25", description = "The duration of the track in minutes and seconds")
        private String duration_mins;

        public TrackInfo() {
        }

        public TrackInfo(String id, String name, Album album, ArrayList<Artist> artists, Integer duration_ms) {
                this.id = id;
                this.name = name;
                this.album = album;
                this.artists = artists;
                this.duration_ms = duration_ms;
                this.duration_mins = calculateDurationMins(duration_ms);
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public Album getAlbum() {
                return album;
        }

        public void setAlbum(Album album) {
                this.album = album;
        }

        public ArrayList<Artist> getArtists() {
                return artists;
        }

        public void setArtists(ArrayList<Artist> artists) {
                this.artists = artists;
        }

        public Integer getDuration_ms() {
                return duration_ms;
        }

        public void setDuration_ms(Integer duration_ms) {
                this.duration_ms = duration_ms;
                this.duration_mins = calculateDurationMins(duration_ms);
        }

        public String getDuration_mins() {
                return duration_mins;
        }

        public String calculateDurationMins(Integer duration_ms) {
                Duration duration = Duration.ofMillis(duration_ms);
                return String.format("%02d:%02d", duration.toMinutes(), duration.toSecondsPart());

        }

        public record Album(
                        @Schema(example = "Life Will Be", description = "The name of the album") String name) {
        }

        public record Artist(
                        @Schema(example = "Cleo Sol", description = "The name of the artist") String name) {
        }

}
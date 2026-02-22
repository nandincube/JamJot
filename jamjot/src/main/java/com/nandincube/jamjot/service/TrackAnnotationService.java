package com.nandincube.jamjot.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.nandincube.jamjot.dto.PlaylistTrackDTO;
import com.nandincube.jamjot.dto.TrackDTO;
import com.nandincube.jamjot.dto.TrackInfo;
import com.nandincube.jamjot.dto.GetTracksResponse;

import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.exceptions.TrackNotFoundException;
import com.nandincube.jamjot.exceptions.UserNotFoundException;

import com.nandincube.jamjot.model.Playlist;
import com.nandincube.jamjot.model.PlaylistMember;
import com.nandincube.jamjot.model.PlaylistMemberID;
import com.nandincube.jamjot.model.Track;

@Service
public class TrackAnnotationService {
    private final PlaylistMemberService playlistMemberService;
    private final TrackService trackService;
    private final PlaylistService playlistService;
    private final RestClient restClient;
    private final PlaylistAnnotationService playlistAnnotationService;
    private static final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1";

    public TrackAnnotationService(PlaylistService playlistService,
            PlaylistMemberService playlistMemberService,
            TrackService trackService,
            RestClient restClient,
            PlaylistAnnotationService playlistAnnotationService) {
        this.playlistService = playlistService;
        this.playlistMemberService = playlistMemberService;
        this.trackService = trackService;
        this.restClient = restClient;
        this.playlistAnnotationService = playlistAnnotationService;
    }

    /**
     * This method retrieves track details from Spotify API given a track ID.
     * 
     * @param trackID - The Spotify ID of the track.
     * @return TrackInfo - The details of the track.
     */
    private TrackInfo getTrackInfoFromSpotify(String trackID) {
        String trackURL = SPOTIFY_BASE_URL + "/tracks/" + trackID;

        TrackInfo spotifyTrackDTO = restClient.get()
                .uri(trackURL)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (req, res) -> {
                            new RuntimeException(new TrackNotFoundException());
                        })
                .body(TrackInfo.class);

        if (spotifyTrackDTO == null) {
            throw new RuntimeException("Failed to retrieve track details from Spotify API");
        }

        return spotifyTrackDTO;
    }

    /**
     * This method retrieves all tracks from a Spotify playlist given the playlist
     * ID.
     * 
     * @param playlistID - The Spotify ID of the playlist.
     * @return ArrayList<TrackDTO> - A list of track details.
     */
    public ArrayList<TrackDTO> getPlaylistTracksInfoFromSpotify(String playlistID) {
        ArrayList<TrackDTO> trackDTOs = new ArrayList<>();
        String next = SPOTIFY_BASE_URL + "/playlists/" + playlistID + "/items";

        do {
            GetTracksResponse response = restClient.get()
                    .uri(next)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            (req, res) -> {
                                new RuntimeException(new PlaylistNotFoundException());
                            })
                    .body(GetTracksResponse.class);

            if (response == null) {
                throw new RuntimeException("Failed to retrieve tracks from Spotify API");
            }

            for (int i = 0; i < response.items().size(); i++) {
                PlaylistTrackDTO apiTrackDTO = response.items().get(i);
                TrackDTO jamjotTrackDTO = new TrackDTO(i+1, apiTrackDTO);
                trackDTOs.add(jamjotTrackDTO);
            }

            next = response.next();

        } while (next != null);

        return trackDTOs;

    }

    /**
     * This method checks if a track exists in the user's spotify at the given track
     * number position.
     * 
     * @param playlistID  - The Spotify ID of the playlist.
     * @param userID      - The ID of the user.
     * @param trackID     - The Spotify ID of the track.
     * @param trackNumber - The track number of the track in the playlist.
     * @return
     */
    protected boolean playlistTrackExistsOnSpotify(String playlistID, String userID, String trackID,
            Integer trackNumber) {
        ArrayList<TrackDTO> tracks = getPlaylistTracksInfoFromSpotify(playlistID);

        Boolean exists = tracks.stream().anyMatch(
                (t) -> t.entry().item().id().equals(trackID) && t.track_number() == trackNumber);
        return exists;
    }

    /**
     * This method retrieves a track (at a given position) from a user's playlist in
     * the database.
     * 
     * @param userID      - ID of the authenticated user.
     * @param playlistID  - Spotify ID of the playlist.
     * @param trackID     - Spotify ID of the track.
     * @param trackNumber - The track number of the track in the playlist.
     * @return PlaylistMember - The track entity from the database.
     * @throws PlaylistNotFoundException if the playlist does not exist in the
     *                                   database.
     * @throws TrackNotFoundException    if the track (at the given position) does
     *                                   not exist in the playlist in the database.
     */
    protected PlaylistMember getPlaylistTrackFromDB(String userID, String playlistID, String trackID,
            Integer trackNumber)
            throws PlaylistNotFoundException, TrackNotFoundException {

        if (!playlistAnnotationService.playlistExistsInDB(userID, playlistID)) {
            throw new PlaylistNotFoundException();
        }

        PlaylistMemberID playlistTrackID = new PlaylistMemberID(trackID, playlistID, trackNumber);
        PlaylistMember trackInPlaylist = playlistMemberService.findById(playlistTrackID)
                .orElseThrow(TrackNotFoundException::new);

        return trackInPlaylist;
    }

    /**
     * This method retrieves the note/annotation for a specific track in a user's
     * playlist.
     * 
     * @param userID      - ID of the authenticated user.
     * @param playlistID  - Spotify ID of the playlist.
     * @param trackID     - Spotify ID of the track.
     * @param trackNumber - The track number of the track in the playlist.
     * @return String - The note/annotation for the track.
     * @throws TrackNotFoundException    if the track (at the given position) does
     *                                   not exist in the playlist in the database
     *                                   or on Spotify.
     * @throws PlaylistNotFoundException if the playlist does not exist in the
     *                                   database or on Spotify.
     */
    public String getTrackNote(String userID, String playlistID, String trackID, Integer trackNumber)
            throws TrackNotFoundException, PlaylistNotFoundException {

        PlaylistMember trackInPlaylist;
        try {
            trackInPlaylist = getPlaylistTrackFromDB(userID, playlistID, trackID, trackNumber);
            return trackInPlaylist.getNote();
        } catch (PlaylistNotFoundException e) {
            if (playlistAnnotationService.playlistExistsOnSpotify(playlistID, userID)) { // playlist exists on spotify
                                                                                         // but no info is stored in
                                                                                         // jamjot db
                return "";
            } else {
                throw new PlaylistNotFoundException();
            }
        } catch (TrackNotFoundException e) {
            if (playlistTrackExistsOnSpotify(playlistID, userID, trackID, trackNumber)) { // track exists in playlist on
                                                                                          // spotify but no info is
                                                                                          // stored in jamjot db
                return "";
            } else {
                throw new TrackNotFoundException();
            }
        }

    }

    /**
     * This method updates the note/annotation for a specific track in a user's
     * playlist.
     * 
     * @param userID      - ID of the authenticated user.
     * @param playlistID  - Spotify ID of the playlist.
     * @param trackID     - Spotify ID of the track.
     * @param trackNumber - The track number of the track in the playlist.
     * @param note        - The new note/annotation for the track.
     * @return PlaylistMember - The updated track entity.
     * @throws PlaylistNotFoundException if the playlist does not exist in the
     *                                   jamjot DB
     * @throws TrackNotFoundException    if the track does not exist as a member in
     *                                   the playlist in the jamjot DB
     * @throws UserNotFoundException     if the user does not exist in the jamjot DB
     */
    public PlaylistMember editTrackNote(String userID, String playlistID, String trackID, Integer trackNumber,
            String note) throws PlaylistNotFoundException, TrackNotFoundException, UserNotFoundException {

        try {
            PlaylistMember trackInPlaylist = getPlaylistTrackFromDB(userID, playlistID, trackID, trackNumber);
            trackInPlaylist.setNote(note);
            return playlistMemberService.save(trackInPlaylist);
        } catch (PlaylistNotFoundException e) { // Playlist does not exist in jamjot DB
            playlistAnnotationService.saveNewPlaylistEntity(userID, playlistID);
        } catch (TrackNotFoundException e) { // Track does not exist in jamjot DB
            saveNewPlaylistTrackEntity(userID, playlistID, trackID, trackNumber);
        }

        return editTrackNote(userID, playlistID, trackID, trackNumber, note);

    }

    /**
     * This method saves a new track entity in the jamjot DB and associates it with
     * the given playlist and position in the playlist.
     * 
     * @param userID      - ID of the authenticated user.
     * @param playlistID  - Spotify ID of the playlist.
     * @param trackID     - Spotify ID of the track.
     * @param trackNumber - The track number of the track in the playlist.
     * @throws PlaylistNotFoundException - If the playlist does not exist on Spotify
     *                                   or in the jamjot DB.
     * @throws TrackNotFoundException    - If the track does not exist on Spotify or
     *                                   in the jamjot DB.
     * @throws UserNotFoundException     - If the user does not exist in the jamjot
     *                                   DB.
     */
    protected void saveNewPlaylistTrackEntity(String userID, String playlistID, String trackID,
            Integer trackNumber) throws PlaylistNotFoundException, TrackNotFoundException, UserNotFoundException {

        if (playlistTrackExistsOnSpotify(playlistID, userID, trackID, trackNumber)) {
            Track track = saveNewTrackEntity(trackID);
            Playlist playlist = playlistAnnotationService.getPlaylistFromDB(userID, playlistID);
            saveTrackPlaylistRelationship(track, playlist, trackNumber);
        } else {
            throw new TrackNotFoundException();
        }
    }

    /**
     * This method creates and saves a new track entity in the jamjot DB. This track
     * can be associated with one or more playlists.
     * 
     * @param trackID - Spotify ID of the track.
     * @return Track - The newly created track entity.
     */
    private Track saveNewTrackEntity(String trackID) {
        TrackInfo trackDTO = getTrackInfoFromSpotify(trackID);
        String trackName = trackDTO.name();
        String artists = trackDTO.artists().stream()
                .map(artist -> artist.name())
                .collect(Collectors.joining(", "));

        Track newTrack = new Track(trackID, trackName, artists, Duration.ofMillis(trackDTO.duration_ms()));
        return trackService.save(newTrack);
    }

    /**
     * This method creates and saves the relationship between a track and a playlist
     * at a specific position.
     * 
     * @param track       - The track entity.
     * @param playlist    - The playlist entity.
     * @param trackNumber - The position of the track in the playlist (track
     *                    number).
     */
    private void saveTrackPlaylistRelationship(Track track, Playlist playlist, Integer trackNumber) {
        PlaylistMember trackInPlaylist = new PlaylistMember(track, playlist, trackNumber);
        playlistMemberService.save(trackInPlaylist);
        track.addToPlaylist(trackInPlaylist);
        playlist.addTrack(trackInPlaylist);
        playlistService.save(playlist);
        trackService.save(track);
    }

    /**
     * This method deletes the note/annotation for a specific track in a user's
     * playlist.
     * 
     * @param userID      - ID of the authenticated user.
     * @param playlistID  - Spotify ID of the playlist.
     * @param trackID     - Spotify ID of the track.
     * @param trackNumber - The track number of the track in the playlist.
     * @return PlaylistMember - The updated playlist track entity with the note
     *         deleted.
     * @throws PlaylistNotFoundException - If the playlist does not exist on Spotify
     *                                   or in the jamjot DB.
     * @throws TrackNotFoundException    - If the track does not exist on Spotify or
     *                                   in the jamjot DB.
     * @throws UserNotFoundException     - If the user does not exist in the jamjot
     *                                   DB.
     */
    public PlaylistMember deleteTrackNote(String userID, String playlistID, String trackID, Integer trackNumber)
            throws PlaylistNotFoundException, TrackNotFoundException, UserNotFoundException {
        return editTrackNote(userID, playlistID, trackID, trackNumber, "");
    }

}

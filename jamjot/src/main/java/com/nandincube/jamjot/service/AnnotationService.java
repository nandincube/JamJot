package com.nandincube.jamjot.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;

import com.nandincube.jamjot.model.Playlist;
import com.nandincube.jamjot.model.PlaylistMember;
import com.nandincube.jamjot.model.PlaylistMemberID;
import com.nandincube.jamjot.repository.PlaylistMemberRepository;
import com.nandincube.jamjot.repository.PlaylistRepository;
import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.exceptions.TrackNotFoundException;
import com.nandincube.jamjot.dto.PlaylistDTO;
import com.nandincube.jamjot.dto.PlaylistsResponse;
import com.nandincube.jamjot.dto.TrackDTO;
import com.nandincube.jamjot.dto.TracksResponse;

@Service
public class AnnotationService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistMemberRepository playlistMemberRepository;
    private final RestClient restClient;

    private static final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1";

    public AnnotationService(PlaylistRepository playlistRepository, PlaylistMemberRepository playlistMemberRepository,
            RestClient restClient) {
        this.playlistRepository = playlistRepository;
        this.playlistMemberRepository = playlistMemberRepository;
        this.restClient = restClient;
    }

    /*   * TODO:
     * 
     * https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html#oauth2-client-access-protected-resources-current-user
     * ///GET users/{user_id}/playlists
     * //OR
     * //GET /me/playlists
     * ///
     * //response.items.forEach(playlist -> {
     * // System.out.println(playlist.name);
     * // System.out.println(playlist.id)}); */
    public ArrayList<PlaylistDTO> getPlaylists() {
        ArrayList<PlaylistDTO> playlistDTOs = new ArrayList<>();
        String next = SPOTIFY_BASE_URL + "/me/playlists";
        do {

            PlaylistsResponse response = restClient.get()
                    .uri(next)
                    .retrieve()
                    .body(PlaylistsResponse.class);

            if(response == null) {
                break;
            }

            playlistDTOs.addAll(response.items());
            next = response.next();

        } while (next != null);

       return playlistDTOs;
    }

    /**
     *   * TODO:
     * 
     * ///GET /playlists/{playlist_id}/tracks
     * ///
     * //res = response.items
     * // for (int i =0 ; i< res.length; i++){
     * // track = res[i].track
     * // System.out.println(track.trackObject.name);
     * // System.out.println(track.trackObject.id)})
     * // };
     * 
     * 
     * 
     * @return
     * @param playlistID
     * @return
     */
    public ArrayList<TrackDTO> getTracks(String playlistID) {
   
        ArrayList<TrackDTO> trackDTOs = new ArrayList<>();


         String next = SPOTIFY_BASE_URL + "/playlists/" + playlistID + "/tracks";
        do {

            TracksResponse response = restClient.get()
                    .uri(next)
                    .retrieve()
                    .body(TracksResponse.class);

            if(response == null) {
                break;
            }

            trackDTOs.addAll(response.items());
            next = response.next();

        } while (next != null);

         return trackDTOs;

    }

    private Playlist getPlaylist(String userID, String playlistID) throws PlaylistNotFoundException {
        Optional<Playlist> playlistOptional = playlistRepository.findById(playlistID);

        if (playlistOptional.isEmpty()) {
            throw new PlaylistNotFoundException();
        }

        Playlist playlist = playlistOptional.get();
        String playlistOwnerID = playlist.getUser().getUserID();

        if (!userID.equals(playlistOwnerID)) {
            throw new PlaylistNotFoundException();
        }
        return playlistOptional.get();
    }

    public String getPlaylistNote(String userID, String playlistID) throws PlaylistNotFoundException {
        Playlist playlist = getPlaylist(userID, playlistID);
        return playlist.getNote();
    }

    public Playlist updatePlaylistNote(String userID, String playlistID, String note) throws PlaylistNotFoundException {
        Playlist playlist = getPlaylist(userID, playlistID);
        playlist.setNote(note);

        return playlistRepository.save(playlist);
    }

    public Playlist deletePlaylistNote(String userID, String playlistID) throws PlaylistNotFoundException {
        return updatePlaylistNote(userID, playlistID, null);
    }

    private PlaylistMember getTrack(String userID, String playlistID, String trackID, Integer trackNumber)
            throws PlaylistNotFoundException, TrackNotFoundException {
        if (getPlaylist(userID, playlistID) == null)
            return null;
        PlaylistMemberID playlistMemberID = new PlaylistMemberID(playlistID, trackID, trackNumber);
        Optional<PlaylistMember> trackInPlaylist = playlistMemberRepository.findById(playlistMemberID);

        if (trackInPlaylist.isEmpty()) {
            throw new TrackNotFoundException();
        }

        return trackInPlaylist.get();
    }

    public String getTrackNote(String userID, String playlistID, String trackID, Integer trackNumber)
            throws TrackNotFoundException, PlaylistNotFoundException {
        if (getPlaylist(userID, playlistID) == null)
            return null;
        PlaylistMember trackInPlaylist = getTrack(userID, playlistID, trackID, trackNumber);
        return trackInPlaylist.getNote();
    }

    public PlaylistMember updateTrackNote(String userID, String playlistID, String trackID, Integer trackNumber,
            String note) throws PlaylistNotFoundException, TrackNotFoundException {
        if (getPlaylist(userID, playlistID) == null)
            return null;

        PlaylistMember trackInPlaylist = getTrack(userID, playlistID, trackID, trackNumber);
        trackInPlaylist.setNote(note);
        return playlistMemberRepository.save(trackInPlaylist);
    }

    public PlaylistMember deleteTrackNote(String userID, String playlistID, String trackID, Integer trackNumber)
            throws PlaylistNotFoundException, TrackNotFoundException {
        return updateTrackNote(userID, playlistID, trackID, trackNumber, null);
    }
}

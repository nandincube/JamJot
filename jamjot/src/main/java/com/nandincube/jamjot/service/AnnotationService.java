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
import com.nandincube.jamjot.model.User;
import com.nandincube.jamjot.repository.PlaylistMemberRepository;
import com.nandincube.jamjot.repository.PlaylistRepository;
import com.nandincube.jamjot.repository.UserRepository;
import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.exceptions.TrackNotFoundException;
import com.nandincube.jamjot.exceptions.UserNotFoundException;
import com.nandincube.jamjot.dto.PlaylistDTO;
import com.nandincube.jamjot.dto.PlaylistsResponse;
import com.nandincube.jamjot.dto.TrackDTO;
import com.nandincube.jamjot.dto.TracksResponse;

@Service
public class AnnotationService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistMemberRepository playlistMemberRepository;
    private final UserRepository userRepository;
    private final RestClient restClient;

    private static final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1";

    public AnnotationService(PlaylistRepository playlistRepository, PlaylistMemberRepository playlistMemberRepository,
            UserRepository userRepository, RestClient restClient) {
        this.playlistRepository = playlistRepository;
        this.playlistMemberRepository = playlistMemberRepository;
        this.restClient = restClient;
        this.userRepository = userRepository;
    }

    /**
     * This method retrieves all playlists belonging to the authenticated user.
     * This is retrieved via the Spotify API using the user's access token.
     * 
     * @return ArrayList<PlaylistDTO> - List of playlists and their details,
     *         including name and spotify ID.
     */
    public ArrayList<PlaylistDTO> getPlaylistsFromSpotify() {
        ArrayList<PlaylistDTO> playlistDTOs = new ArrayList<>();
        String next = SPOTIFY_BASE_URL + "/me/playlists";
        do {

            PlaylistsResponse response = restClient.get()
                    .uri(next)
                    .retrieve()
                    .body(PlaylistsResponse.class);

            if (response == null) {
                break;
            }

            playlistDTOs.addAll(response.items());
            next = response.next();

        } while (next != null);

        return playlistDTOs;
    }

    /**
     * This method checks if a playlist exists on Spotify and belongs to the user.
     * 
     * @param playlistID - Spotify ID of the playlist.
     * @return boolean - True if the playlist exists and belongs to the user, false
     *         otherwise.
     */
    private boolean playlistBelongsToUser(String playlistID) {
        ArrayList<PlaylistDTO> playlists = getPlaylistsFromSpotify();
        PlaylistDTO playlist = playlists.stream()
                .filter(p -> p.id().equals(playlistID))
                .findAny()
                .orElse(null);
        return playlist != null;
    }


    private PlaylistDTO getPlaylistFromSpotify(String playlistID) {
        String playlistURL = SPOTIFY_BASE_URL +  "/playlists/"+ playlistID;
   

        PlaylistDTO playlistDTO = restClient.get()
                    .uri(playlistURL)
                    .retrieve()
                    .body(PlaylistDTO.class);


        return playlistDTO;
    }



    /**
     * * TODO:
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

            if (response == null) {
                break;
            }

            trackDTOs.addAll(response.items());
            next = response.next();

        } while (next != null);

        return trackDTOs;

    }

    /**
     * This method retrieves a playlist for the user.
     * 
     * @param userID     - ID of the user.
     * @param playlistID - Spotify ID of the playlist.
     * @return Playlist - The playlist entity.
     * @throws PlaylistNotFoundException - If the playlist does not exist or does
     *                                   not belong to the user.
     */
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

    /**
     * This method retrieves the note for a specific playlist for the user.
     * 
     * @param userID     - ID of the user.
     * @param playlistID - Spotify ID of the playlist.
     * @return String - Note associated with the playlist.
     * @throws PlaylistNotFoundException - If the playlist does not exist or does
     *                                   not belong to the user.
     */
    public String getPlaylistNote(String userID, String playlistID) throws PlaylistNotFoundException {
        try {
            Playlist playlist = getPlaylist(userID, playlistID);
            return playlist.getNote();
        } catch (PlaylistNotFoundException e) {
            if (playlistBelongsToUser(playlistID)) { // Playlist exists on Spotify but not in jamjot DB
                return null;
            } else {
                throw new PlaylistNotFoundException();
            }
        }
    }

    public Playlist updatePlaylistNote(String userID, String playlistID, String note) throws PlaylistNotFoundException, UserNotFoundException {
        try {
            Playlist playlist = getPlaylist(userID, playlistID);
            playlist.setNote(note);
            return playlistRepository.save(playlist);
        } catch (PlaylistNotFoundException e) {
            if (playlistExists(playlistID)) { // Playlist exists on Spotify but not in jamjot DB

                Playlist newPlaylist = createNewPlaylistEntity(userID, playlistID, note);
               
                newPlaylist.setNote(note);
                return playlistRepository.save(newPlaylist);

            } else {
                throw new PlaylistNotFoundException();

            }
        }
    }

    public Playlist deletePlaylistNote(String userID, String playlistID) throws PlaylistNotFoundException, UserNotFoundException {
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

    public Playlist createNewPlaylistEntity(String userID, String playlistID, String name) throws UserNotFoundException {
        System.out.println("UserID: " + userID);
        System.out.println("PlaylistID: " + playlistID);
        System.out.println("Name: " + name);
        Optional<User> user = userRepository.findById(userID);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }

        Playlist newPlaylist = new Playlist(playlistID, name, user.get());
        return playlistRepository.save(newPlaylist);

    }
}

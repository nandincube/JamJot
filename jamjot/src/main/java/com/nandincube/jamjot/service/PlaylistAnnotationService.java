package com.nandincube.jamjot.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.nandincube.jamjot.dto.PlaylistDTO;
import com.nandincube.jamjot.dto.GetPlaylistsResponse;
import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.exceptions.UserNotFoundException;
import com.nandincube.jamjot.model.Playlist;
import com.nandincube.jamjot.model.User;
import com.nandincube.jamjot.repository.UserRepository;

@Service
public class PlaylistAnnotationService {
    private final PlaylistService playlistService;
    private final UserRepository userRepository;
    private final RestClient restClient;

    private static final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1";

    public PlaylistAnnotationService(PlaylistService playlistService, 
            UserRepository userRepository,
            RestClient restClient) {
        this.playlistService = playlistService;
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
    public ArrayList<PlaylistDTO> getPlaylistsInfoFromSpotify() {
        ArrayList<PlaylistDTO> playlistDTOs = new ArrayList<>();
        String next = SPOTIFY_BASE_URL + "/me/playlists";
        do {

            GetPlaylistsResponse response = restClient.get()
                    .uri(next)
                    .retrieve()
                    .body(GetPlaylistsResponse.class);

            if (response == null) {
                throw new RuntimeException("Failed to retrieve playlists from Spotify API");
            }

            playlistDTOs.addAll(response.items());
            next = response.next();

        } while (next != null);

        return playlistDTOs;
    }

  
    /**
     * This method retrieves playlist details from Spotify API given a playlist ID.
     * 
     * @param playlistID - The Spotify ID of the playlist.
     * @return PlaylistDTO - The details of the playlist.
     */
    private PlaylistDTO getPlaylistInfoFromSpotify(String playlistID) throws PlaylistNotFoundException {
        String playlistURL = SPOTIFY_BASE_URL + "/playlists/" + playlistID;

        PlaylistDTO playlistDTO = restClient.get()
                .uri(playlistURL)
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND, (req, res) -> {
                        resouceNotFound();
                        }
                            )

                .body(PlaylistDTO.class);

        return playlistDTO;
    }

    private void resouceNotFound() throws PlaylistNotFoundException{
        throw new PlaylistNotFoundException();
    }

    /**
     * This method checks if a playlist exists on Spotify and belongs to the user.
     * 
     * @param playlistID - Spotify ID of the playlist.
     * @param userID     - Spotify ID of the user.
     * @return boolean - True if the playlist exists and belongs to the user, false
     *         otherwise.
     */
    protected boolean playlistExistsOnSpotify(String playlistID, String userID) {
        PlaylistDTO playlist = getPlaylistInfoFromSpotify(playlistID);
        if (playlist == null || !playlist.owner().id().equals(userID)) {
            return false;
        }
        return true;
    }

    /**
     * This method retrieves playlist details from the jamjot DB.
     * 
     * @param userID     - Spotify ID of the user.
     * @param playlistID - Spotify ID of the playlist.
     * @return Playlist - The playlist entity.
     * @throws PlaylistNotFoundException - If the playlist does not exist or does
     *                                   not belong to the user.
     */
    protected Playlist getPlaylistFromDB(String userID, String playlistID) throws PlaylistNotFoundException {
        Optional<Playlist> playlistOptional = playlistService.findById(playlistID);

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
     * This method checks if a playlist exists in the jamjot DB.
     * @param userID - ID of the authenticated user.
     * @param playlistID - Spotify ID of the playlist.
     * @return Boolean - True if the playlist exists in the jamjot DB, false otherwise.
     */
    protected Boolean playlistExistsInDB(String userID, String playlistID) {
        try {
            getPlaylistFromDB(userID, playlistID);
        } catch (PlaylistNotFoundException e) {
            return false;
        }
        return true;
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
            Playlist playlist = getPlaylistFromDB(userID, playlistID);
            return playlist.getNote();
        } catch (PlaylistNotFoundException e) {
            if (playlistExistsOnSpotify(playlistID, userID)) { // Playlist exists on Spotify but not in jamjot DB
                return "";
            } else {
                throw new PlaylistNotFoundException();
            }
        }
    }

    /**
     * This method edits the note for a specific playlist. If the playlist does not exist in the jamjot DB but exists on Spotify, it creates a new playlist entity in the jamjot DB and then updates the note.
     * @param userID - ID of the user.
     * @param playlistID - Spotify ID of the playlist.
     * @param note - The new note to be set for the playlist.
     * @return Playlist - The updated playlist entity.
     * @throws PlaylistNotFoundException - If the playlist does not exist or does not belong to the user.
     * @throws UserNotFoundException - If the user does not exist in the jamjot DB.
     */
    public Playlist editPlaylistNote(String userID, String playlistID, String note)
            throws PlaylistNotFoundException, UserNotFoundException {
        Playlist playlist;
        try {
            playlist = getPlaylistFromDB(userID, playlistID);
        } catch (PlaylistNotFoundException e) {
            if (playlistExistsOnSpotify(playlistID, userID)) { // Playlist exists on Spotify but not in jamjot DB
                playlist = createNewPlaylistEntity(userID, playlistID);
            } else {
                throw new PlaylistNotFoundException();
            }
        }
        playlist.setNote(note);
        return playlistService.save(playlist);
    }

    /**
     * This method creates a new playlist entity given information retreived from Spotify API.
     * @param userID - ID of the user.
     * @param playlistID - Spotify ID of the playlist.
     * @return Playlist - The newly created playlist entity.
     * @throws UserNotFoundException - If the user does not exist in the jamjot DB.
     */
    private Playlist createNewPlaylistEntity(String userID, String playlistID) throws UserNotFoundException {

        PlaylistDTO playlistDTO = getPlaylistInfoFromSpotify(playlistID);

        Optional<User> user = userRepository.findById(userID);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }
        return new Playlist(playlistID, playlistDTO.name(), user.get());
    }

    /**
     * This method deletes the note for a specific playlist by resetting it to an empty string.
     * @param userID - ID of the user.
     * @param playlistID - Spotify ID of the playlist.
     * @return Playlist - The updated playlist entity with the note deleted.
     * @throws PlaylistNotFoundException - If the playlist does not exist or does not belong to the user.
     * @throws UserNotFoundException - If the user does not exist in the jamjot DB.
     */
    public Playlist deletePlaylistNote(String userID, String playlistID)
            throws PlaylistNotFoundException, UserNotFoundException {
        return editPlaylistNote(userID, playlistID, "");
    }


     /**
     * This method creates and saves a new playlist entity in the jamjot DB if it does not exist.
     * @param userID - ID of the authenticated user.
     * @param playlistID - Spotify ID of the playlist.
     * @throws PlaylistNotFoundException - If the playlist does not exist on Spotify or in the jamjot DB.
     * @throws UserNotFoundException - If the user does not exist in the jamjot DB.
     */
    protected void saveNewPlaylistEntity(String userID, String playlistID) throws PlaylistNotFoundException, UserNotFoundException {

        if (playlistExistsOnSpotify(playlistID, userID)) {
            Playlist playlist = createNewPlaylistEntity(userID, playlistID);
            playlistService.save(playlist);
        } else {
            throw new PlaylistNotFoundException();
        }

    }
}

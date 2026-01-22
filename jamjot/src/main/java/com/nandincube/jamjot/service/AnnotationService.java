package com.nandincube.jamjot.service;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;

import com.nandincube.jamjot.model.Playlist;
import com.nandincube.jamjot.model.PlaylistMember;
import com.nandincube.jamjot.model.PlaylistMemberID;
import com.nandincube.jamjot.model.Timestamp;
import com.nandincube.jamjot.model.Track;
import com.nandincube.jamjot.model.User;
import com.nandincube.jamjot.repository.PlaylistMemberRepository;
import com.nandincube.jamjot.repository.PlaylistRepository;
import com.nandincube.jamjot.repository.TimestampRepository;
import com.nandincube.jamjot.repository.TrackRepository;
import com.nandincube.jamjot.repository.UserRepository;
import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.exceptions.TimestampNotFoundException;
import com.nandincube.jamjot.exceptions.TrackNotFoundException;
import com.nandincube.jamjot.exceptions.UserNotFoundException;
import com.nandincube.jamjot.dto.PlaylistDTO;
import com.nandincube.jamjot.dto.PlaylistsResponse;
import com.nandincube.jamjot.dto.SpotifyTrackDTO;
import com.nandincube.jamjot.dto.TrackInfo;
import com.nandincube.jamjot.dto.TrackDTO;
import com.nandincube.jamjot.dto.TracksResponse;

@Service
public class AnnotationService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistMemberRepository playlistMemberRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final TimestampRepository timestampRepository;
    private final RestClient restClient;

    private static final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1";

    public AnnotationService(PlaylistRepository playlistRepository, PlaylistMemberRepository playlistMemberRepository,
            UserRepository userRepository, TrackRepository trackRepository, TimestampRepository timestampRepository,
            RestClient restClient) {
        this.playlistRepository = playlistRepository;
        this.playlistMemberRepository = playlistMemberRepository;
        this.trackRepository = trackRepository;
        this.restClient = restClient;
        this.userRepository = userRepository;
        this.timestampRepository = timestampRepository;
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

    private PlaylistDTO getPlaylistFromSpotify(String playlistID) {
        String playlistURL = SPOTIFY_BASE_URL + "/playlists/" + playlistID;

        PlaylistDTO playlistDTO = restClient.get()
                .uri(playlistURL)
                .retrieve()
                .body(PlaylistDTO.class);

        return playlistDTO;
    }

    /**
     * This method checks if a playlist exists on Spotify and belongs to the user.
     * 
     * @param playlistID - Spotify ID of the playlist.
     * @param userID     - ID of the user.
     * @return boolean - True if the playlist exists and belongs to the user, false
     *         otherwise.
     */
    private boolean playlistExists(String playlistID, String userID) {
        PlaylistDTO playlist = getPlaylistFromSpotify(playlistID);
        if (playlist == null || !playlist.owner().id().equals(userID)) {
            return false;
        }
        return true;
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
    private Playlist getPlaylistFromDB(String userID, String playlistID) throws PlaylistNotFoundException {
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
            Playlist playlist = getPlaylistFromDB(userID, playlistID);
            return playlist.getNote();
        } catch (PlaylistNotFoundException e) {
            if (playlistExists(playlistID, userID)) { // Playlist exists on Spotify but not in jamjot DB
                return "";
            } else {
                throw new PlaylistNotFoundException();
            }
        }
    }

    public Playlist updatePlaylistNote(String userID, String playlistID, String note)
            throws PlaylistNotFoundException, UserNotFoundException {
        Playlist playlist;
        try {
            playlist = getPlaylistFromDB(userID, playlistID);
        } catch (PlaylistNotFoundException e) {
            if (playlistExists(playlistID, userID)) { // Playlist exists on Spotify but not in jamjot DB
                playlist = createNewPlaylistEntity(userID, playlistID);
            } else {
                throw new PlaylistNotFoundException();
            }
        }
        playlist.setNote(note);
        return playlistRepository.save(playlist);
    }

    private Playlist createNewPlaylistEntity(String userID, String playlistID) throws UserNotFoundException {

        PlaylistDTO playlistDTO = getPlaylistFromSpotify(playlistID);

        Optional<User> user = userRepository.findById(userID);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }

        Playlist newPlaylist = new Playlist(playlistID, playlistDTO.name(), user.get());
        return playlistRepository.save(newPlaylist);
    }

    public Playlist deletePlaylistNote(String userID, String playlistID)
            throws PlaylistNotFoundException, UserNotFoundException {
        return updatePlaylistNote(userID, playlistID, "");
    }

    private TrackInfo getTrackFromSpotify(String trackID) {
        String trackURL = SPOTIFY_BASE_URL + "/tracks/" + trackID;

        TrackInfo spotifyTrackDTO = restClient.get()
                .uri(trackURL)
                .retrieve()
                .body(TrackInfo.class);

        return spotifyTrackDTO;
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
    public ArrayList<TrackDTO> getTracksFromSpotify(String playlistID) {
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

            for (int i = 0; i < response.items().size(); i++) {
                SpotifyTrackDTO apiTrackDTO = response.items().get(i);
                trackDTOs.add(new TrackDTO(apiTrackDTO, i + 1));
            }

            next = response.next();

        } while (next != null);

        return trackDTOs;

    }

    private boolean trackExists(String playlistID, String userID, String trackID, Integer trackNumber) {
        ArrayList<TrackDTO> tracks = getTracksFromSpotify(playlistID);
        int count = (int) tracks.stream().filter(t -> t.track_number() == trackNumber && t.track()
                .track().id().equals(trackID)).count();
        return count > 0;
    }

    private PlaylistMember getTrackFromDB(String userID, String playlistID, String trackID, Integer trackNumber)
            throws PlaylistNotFoundException, TrackNotFoundException {

        getPlaylistFromDB(userID, playlistID);
        PlaylistMemberID playlistMemberID = new PlaylistMemberID(trackID, playlistID, trackNumber);
        Optional<PlaylistMember> trackInPlaylist = playlistMemberRepository.findById(playlistMemberID);

        if (trackInPlaylist.isEmpty()) {
            throw new TrackNotFoundException();
        }

        return trackInPlaylist.get();
    }

    // Check DB for note
    public String getTrackNote(String userID, String playlistID, String trackID, Integer trackNumber)
            throws TrackNotFoundException, PlaylistNotFoundException {
        PlaylistMember trackInPlaylist;
        try {
            trackInPlaylist = getTrackFromDB(userID, playlistID, trackID, trackNumber);
        } catch (PlaylistNotFoundException e) {
            if (playlistExists(playlistID, userID)) {
                return "";
            } else {
                throw new PlaylistNotFoundException();
            }
        } catch (TrackNotFoundException e) {
            if (trackExists(playlistID, userID, trackID, trackNumber)) {
                return "";
            } else {
                throw new TrackNotFoundException();
            }
        }

        return trackInPlaylist.getNote();
    }

    public PlaylistMember updateTrackNote(String userID, String playlistID, String trackID, Integer trackNumber,
            String note) throws PlaylistNotFoundException, TrackNotFoundException, UserNotFoundException {
        PlaylistMember trackInPlaylist = null;

        try {
            trackInPlaylist = getTrackFromDB(userID, playlistID, trackID, trackNumber);
            trackInPlaylist.setNote(note);
            return playlistMemberRepository.save(trackInPlaylist);
        } catch (PlaylistNotFoundException e) {
            if (playlistExists(playlistID, userID)) {
                Playlist playlist = createNewPlaylistEntity(userID, playlistID);
                playlistRepository.save(playlist);
                return updateTrackNote(userID, playlistID, trackID, trackNumber, note);
            } else {
                throw new PlaylistNotFoundException();
            }
        } catch (TrackNotFoundException e) {
            if (trackExists(playlistID, userID, trackID, trackNumber)) {
                Track track = createNewTrackEntity(trackID, trackNumber);
                Playlist playlist = getPlaylistFromDB(userID, playlistID);
                createTrackPlaylistRelationship(track, playlist, trackNumber);
                return updateTrackNote(userID, playlistID, trackID, trackNumber, note);
            } else {
                throw new TrackNotFoundException();
            }
        }

    }

    private Track createNewTrackEntity(String trackID, int trackNumber) throws UserNotFoundException {
        TrackInfo trackDTO = getTrackFromSpotify(trackID);
        String trackName = trackDTO.name();
        String artists = trackDTO.artists().stream()
                .map(artist -> artist.name())
                .collect(Collectors.joining(", "));

        Track newTrack = new Track(trackID, trackName, artists);
        return trackRepository.save(newTrack);

    }

    private void createTrackPlaylistRelationship(Track track, Playlist playlist, Integer trackNumber) {
        PlaylistMember playlistMember = new PlaylistMember(track, playlist, trackNumber);
        playlistMemberRepository.save(playlistMember);
        track.addToPlaylist(playlistMember);
        trackRepository.save(track);
    }

    public PlaylistMember deleteTrackNote(String userID, String playlistID, String trackID, Integer trackNumber)
            throws PlaylistNotFoundException, TrackNotFoundException, UserNotFoundException {
        return updateTrackNote(userID, playlistID, trackID, trackNumber, "");
    }





    /**
     * TODO: check thisss
     * @param userID
     * @param playlistID
     * @param trackID
     * @param trackNumber
     * @return
     * @throws TrackNotFoundException
     * @throws PlaylistNotFoundException
     */
    public List<Timestamp> getTimestampNotes(String userID, String playlistID, String trackID, Integer trackNumber)
            throws TrackNotFoundException, PlaylistNotFoundException, UserNotFoundException {
        PlaylistMember trackInPlaylist;
        try {
            trackInPlaylist = getTrackFromDB(userID, playlistID, trackID, trackNumber);
            return trackInPlaylist.getTimestamps();

        } catch (PlaylistNotFoundException e) {
            if (playlistExists(playlistID, userID)) {
                return new ArrayList<>();
            } else {
                throw new PlaylistNotFoundException();
            }
        } catch (TrackNotFoundException e) {
            if (trackExists(playlistID, userID, trackID, trackNumber)) {
                return new ArrayList<>();
            } else {
                throw new TrackNotFoundException();
            }
        }
    }


    public Timestamp addTimestampNote(String userID, String playlistID, String trackID, Integer trackNumber,
            String startInMins, String endInMins, String note)
            throws PlaylistNotFoundException, TrackNotFoundException, UserNotFoundException {

        PlaylistMember trackInPlaylist = null;

        try {
            trackInPlaylist = getTrackFromDB(userID, playlistID, trackID, trackNumber);
            return createNewTimestamp(startInMins, endInMins, note, trackInPlaylist, trackID);
        } catch (PlaylistNotFoundException e) {
            if (playlistExists(playlistID, userID)) {
                Playlist playlist = createNewPlaylistEntity(userID, playlistID);
                playlistRepository.save(playlist);
                return addTimestampNote(userID, playlistID, trackID, trackNumber, startInMins, endInMins, note);
            } else {
                throw new PlaylistNotFoundException();
            }
        } catch (TrackNotFoundException e) {
            if (trackExists(playlistID, userID, trackID, trackNumber)) {
                Track track = createNewTrackEntity(trackID, trackNumber);
                Playlist playlist = getPlaylistFromDB(userID, playlistID);
                createTrackPlaylistRelationship(track, playlist, trackNumber);
                return addTimestampNote(userID, playlistID, trackID, trackNumber, startInMins, endInMins, note);
            } else {
                throw new TrackNotFoundException();
            }
        }
    }

    

    public Timestamp updateTimestampNote(String userID, String playlistID, String trackID, Integer trackNumber,
            String note, String timestampID) throws PlaylistNotFoundException, TrackNotFoundException, UserNotFoundException, TimestampNotFoundException {

        PlaylistMember trackInPlaylist = null;

        try {
            trackInPlaylist = getTrackFromDB(userID, playlistID, trackID, trackNumber);
            Optional<Timestamp> timestampOptional = timestampRepository.findByIdAndPlaylistMember(timestampID, trackInPlaylist);
            if (timestampOptional.isEmpty()) {
                throw new TimestampNotFoundException();
            }
            Timestamp timestamp = timestampOptional.get();
            timestamp.setNote(note);
            return timestampRepository.save(timestamp);

        } catch (PlaylistNotFoundException e) {
            if (playlistExists(playlistID, userID)) {
                Playlist playlist = createNewPlaylistEntity(userID, playlistID);
                playlistRepository.save(playlist);
                return updateTimestampNote(userID, playlistID, trackID, trackNumber, note, timestampID);
            } else {
                throw new PlaylistNotFoundException();
            }
        } catch (TrackNotFoundException e) {
            if (trackExists(playlistID, userID, trackID, trackNumber)) {
                Track track = createNewTrackEntity(trackID, trackNumber);
                Playlist playlist = getPlaylistFromDB(userID, playlistID);
                createTrackPlaylistRelationship(track, playlist, trackNumber);
                return updateTimestampNote(userID, playlistID, trackID, trackNumber, note, timestampID);
            } else {
                throw new TrackNotFoundException();
            }
        } catch (TimestampNotFoundException e) {
            throw new TimestampNotFoundException();
        }
    }

    public void deleteTimestampNote(String userID, String playlistID, String trackID, Integer trackNumber,
            String timestampID) throws PlaylistNotFoundException, TrackNotFoundException, UserNotFoundException, TimestampNotFoundException {

        PlaylistMember trackInPlaylist = getTrackFromDB(userID, playlistID, trackID, trackNumber);
        Optional<Timestamp> timestampOptional = timestampRepository.findByIdAndPlaylistMember(timestampID, trackInPlaylist);
        if (timestampOptional.isEmpty()) {
            throw new TimestampNotFoundException();
        }
        Timestamp timestamp = timestampOptional.get();
        timestampRepository.delete(timestamp);
    }





    private Timestamp createNewTimestamp(String startInMins, String endInMins, String note, PlaylistMember playlistMember,
            String trackID) {
        TrackInfo trackInfo = getTrackFromSpotify(trackID);
        startInMins = startInMins.trim();
        endInMins = endInMins.trim();
        Pattern pattern = Pattern.compile("^(\\d{1,2}):(\\d{2})$");
        Matcher startMatcher = pattern.matcher(startInMins);
        Matcher endMatcher = pattern.matcher(endInMins);

    
        if(!startMatcher.find()) {
            throw new IllegalArgumentException("Start time must be in the format mm:ss");
        }

        if(!endMatcher.find()) {
            throw new IllegalArgumentException("End time must be in the format mm:ss");
        }


        Duration startDuration = Duration.ofMinutes(Long.parseLong(startMatcher.group(1))).plusSeconds(Long.parseLong(startMatcher.group(2)));
        Duration endDuration = Duration.ofMinutes(Long.parseLong(endMatcher.group(1))).plusSeconds(Long.parseLong(endMatcher.group(2)));
        Duration trackDuration = Duration.ofMillis(trackInfo.duration_ms());

        if (startDuration.isNegative() || endDuration.isNegative()) {
            throw new IllegalArgumentException("Start time and end time must be non-negative");
        }
        if (startDuration.compareTo(endDuration) > 0) {
            throw new IllegalArgumentException("Start time must be less than end time");
        }

        if (endDuration.compareTo(trackDuration) >= 0) {
            throw new IllegalArgumentException("End time must be less than track duration");
        }

        Timestamp timestamp = new Timestamp(startDuration, endDuration, note, playlistMember);
        return timestampRepository.save(timestamp);
    }

}

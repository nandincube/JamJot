package com.nandincube.jamjot.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nandincube.jamjot.Model.Playlist;
import com.nandincube.jamjot.Model.PlaylistMember;
import com.nandincube.jamjot.Model.PlaylistMemberID;
import com.nandincube.jamjot.Repository.PlaylistMemberRepository;
import com.nandincube.jamjot.Repository.PlaylistRepository;
import com.nandincube.jamjot.Exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.Exceptions.TrackNotFoundException;

@Service
public class AnnotationService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistMemberRepository playlistMemberRepository;

    public AnnotationService(PlaylistRepository playlistRepository, PlaylistMemberRepository playlistMemberRepository) {
        this.playlistRepository = playlistRepository;
        this.playlistMemberRepository = playlistMemberRepository;
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

    private PlaylistMember getTrack(String userID, String playlistID, String trackID, Integer trackNumber) throws PlaylistNotFoundException, TrackNotFoundException {
        if (getPlaylist(userID, playlistID) == null) return null;
        PlaylistMemberID playlistMemberID = new PlaylistMemberID(playlistID, trackID, trackNumber);
        Optional<PlaylistMember> trackInPlaylist = playlistMemberRepository.findById(playlistMemberID);

       if(trackInPlaylist.isEmpty()){
            throw new TrackNotFoundException();
       }

       return trackInPlaylist.get();  
    }

     public String getTrackNote(String userID, String playlistID, String trackID, Integer trackNumber) throws TrackNotFoundException, PlaylistNotFoundException {
        if (getPlaylist(userID, playlistID) == null) return null;
        PlaylistMember trackInPlaylist = getTrack(userID, playlistID, trackID, trackNumber);
        return trackInPlaylist.getNote();
    }

    public PlaylistMember updateTrackNote(String userID, String playlistID, String trackID, Integer trackNumber, String note) throws PlaylistNotFoundException, TrackNotFoundException {
        if (getPlaylist(userID, playlistID) == null) return null;

        PlaylistMember trackInPlaylist = getTrack(userID, playlistID, trackID, trackNumber);
        trackInPlaylist.setNote(note);
        return playlistMemberRepository.save(trackInPlaylist);
    }

    public PlaylistMember deleteTrackNote(String userID, String playlistID, String trackID, Integer trackNumber) throws PlaylistNotFoundException, TrackNotFoundException {
        return updateTrackNote(userID, playlistID, trackID, trackNumber, null);
    }
}

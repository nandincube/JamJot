package com.nandincube.jamjot.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nandincube.jamjot.Model.Playlist;
import com.nandincube.jamjot.Model.PlaylistMember;
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

    private PlaylistMember getTrack(String userID, String playlistID, Integer trackNumber) throws PlaylistNotFoundException, TrackNotFoundException {
       Playlist playlist = getPlaylist(userID, playlistID);

       Optional<PlaylistMember> trackInPlaylist = playlistMemberRepository.findByPlaylistIDAndTrackNumber(playlist.getPlaylistID(), trackNumber);

       if(trackInPlaylist.isEmpty()){
            throw new TrackNotFoundException();
       }

       return trackInPlaylist.get();  
    }

     public String getTrackNote(String userID, String playlistID, Integer trackNumber) throws TrackNotFoundException, PlaylistNotFoundException {
        PlaylistMember trackInPlaylist = getTrack(userID, playlistID, trackNumber);
        return trackInPlaylist.getNote();
    }

    public PlaylistMember updateTrackNote(String userID, String playlistID, Integer trackNumber, String note) throws PlaylistNotFoundException, TrackNotFoundException {
        PlaylistMember trackInPlaylist = getTrack(userID, playlistID, trackNumber);
        trackInPlaylist.setNote(note);
        return playlistMemberRepository.save(trackInPlaylist);
    }

    public PlaylistMember deletePlaylistNote(String userID, String playlistID, Integer trackNumber) throws PlaylistNotFoundException, TrackNotFoundException {
        return updateTrackNote(userID, playlistID, trackNumber, null);
    }

}

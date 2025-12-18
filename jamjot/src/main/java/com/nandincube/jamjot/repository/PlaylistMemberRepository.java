package com.nandincube.jamjot.Repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nandincube.jamjot.Model.PlaylistMember;
import com.nandincube.jamjot.Model.PlaylistMemberID;

public interface PlaylistMemberRepository extends JpaRepository<PlaylistMember, PlaylistMemberID> {
    //List<PlaylistMember> findByPlaylistID(String playlistID);
   // Optional<PlaylistMember> findByPlaylist_PlaylistIDAndTrackNumber(String playlistID, Integer trackNumber);
    Optional<PlaylistMember> findById(PlaylistMemberID playlistMemberID);
}
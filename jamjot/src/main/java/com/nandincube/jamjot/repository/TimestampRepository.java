package com.nandincube.jamjot.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nandincube.jamjot.model.PlaylistMemberID;
import com.nandincube.jamjot.model.Timestamp;

public interface TimestampRepository extends JpaRepository<Timestamp, Long> {
    Optional<Timestamp> findByTimestampIDAndPlaylistMember_PlaylistMemberID_(Long timestampID, PlaylistMemberID playlistMemberID);
}
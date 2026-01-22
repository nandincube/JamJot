package com.nandincube.jamjot.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nandincube.jamjot.model.PlaylistMember;
import com.nandincube.jamjot.model.Timestamp;

public interface TimestampRepository extends JpaRepository<Timestamp, String> {
    Optional<Timestamp> findByIdAndPlaylistMember(String id, PlaylistMember playlistMember);
}
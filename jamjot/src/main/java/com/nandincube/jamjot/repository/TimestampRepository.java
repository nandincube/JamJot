package com.nandincube.jamjot.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nandincube.jamjot.model.PlaylistMember;
import com.nandincube.jamjot.model.Timestamp;

public interface TimestampRepository extends JpaRepository<Timestamp, Long> {
    Optional<Timestamp> findByIdAndPlaylistMember(Long id, PlaylistMember playlistMember);
}
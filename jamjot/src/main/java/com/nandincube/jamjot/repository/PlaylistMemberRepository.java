package com.nandincube.jamjot.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nandincube.jamjot.model.PlaylistMember;
import com.nandincube.jamjot.model.PlaylistMemberID;

public interface PlaylistMemberRepository extends JpaRepository<PlaylistMember, PlaylistMemberID> {
    Optional<PlaylistMember> findById(PlaylistMemberID playlistMemberID);
}
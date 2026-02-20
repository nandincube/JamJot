package com.nandincube.jamjot.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nandincube.jamjot.model.Timestamp;

public interface TimestampRepository extends JpaRepository<Timestamp, Long> {
    //Optional<Timestamp> findByTimestampIDAndUser_UserID(Long timestampID, String userID);


    @Query
    ("""
            SELECT t 
            FROM Timestamp t JOIN Playlist p ON t.playlist_id = p.playlist_id
            WHERE t.timestamp_id = ?1 AND p.user_id = ?2
            """)
    Optional<Timestamp> findByTimestampIDAndUserID(Long timestampID, String userID);
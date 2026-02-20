package com.nandincube.jamjot.repository;


import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nandincube.jamjot.model.Timestamp;

public interface TimestampRepository extends JpaRepository<Timestamp, Long> {
    //Optional<Timestamp> findByTimestampIDAndUser_UserID(Long timestampID, String userID);


    @Query
    ("""
            SELECT t 
            FROM Timestamp t 
            JOIN t.playlistMember pm
            JOIN pm.playlist p
            WHERE t.timestampID = ?1 AND p.user.userID = ?2
            """)
    Optional<Timestamp> findByTimestampIDAndUserID(Long timestampID, String userID); 

    @Query(
        """          
                SELECT t
                FROM Timestamp t
                WHERE t.playlistMember.playlist.playlistID = ?2
                AND t.playlistMember.playlist.user.userID = ?1
                AND t.playlistMember.track.trackID = ?3
                AND t.playlistMember.trackNumber = ?4
    """
    )
    ArrayList<Timestamp> findByPlaylistMemberID(String userID, String playlistID, String trackID, int trackNumber);
        
}
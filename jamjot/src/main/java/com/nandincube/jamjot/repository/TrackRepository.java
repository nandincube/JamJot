package com.nandincube.jamjot.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.nandincube.jamjot.model.Track;

public interface TrackRepository extends JpaRepository<Track, String> {
    
}
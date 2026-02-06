package com.nandincube.jamjot.service;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;

import com.nandincube.jamjot.model.Playlist;
import com.nandincube.jamjot.model.PlaylistMember;
import com.nandincube.jamjot.model.PlaylistMemberID;
import com.nandincube.jamjot.model.Timestamp;
import com.nandincube.jamjot.model.Track;
import com.nandincube.jamjot.model.User;
import com.nandincube.jamjot.repository.PlaylistMemberRepository;
import com.nandincube.jamjot.repository.PlaylistRepository;
import com.nandincube.jamjot.repository.TimestampRepository;
import com.nandincube.jamjot.repository.TrackRepository;
import com.nandincube.jamjot.repository.UserRepository;
import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.exceptions.TimestampNotFoundException;
import com.nandincube.jamjot.exceptions.TrackNotFoundException;
import com.nandincube.jamjot.exceptions.UserNotFoundException;
import com.nandincube.jamjot.dto.PlaylistDTO;
import com.nandincube.jamjot.dto.GetPlaylistsResponse;
import com.nandincube.jamjot.dto.SpotifyTrackDTO;
import com.nandincube.jamjot.dto.TimestampDTO;
import com.nandincube.jamjot.dto.TimestampResponse;
import com.nandincube.jamjot.dto.TrackInfo;
import com.nandincube.jamjot.dto.TrackDTO;
import com.nandincube.jamjot.dto.GetTracksResponse;

@Service
public class AnnotationService {
 



}

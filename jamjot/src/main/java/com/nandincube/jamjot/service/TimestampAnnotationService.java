package com.nandincube.jamjot.service;

import org.springframework.stereotype.Service;
import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.exceptions.TimestampNotFoundException;
import com.nandincube.jamjot.exceptions.TrackNotFoundException;
import com.nandincube.jamjot.exceptions.UserNotFoundException;
import com.nandincube.jamjot.model.Playlist;
import com.nandincube.jamjot.model.PlaylistMember;
import com.nandincube.jamjot.model.Timestamp;

@Service
public class TimestampAnnotationService {

    private final TimestampService timestampService;
    private final TrackAnnotationService trackAnnotationService;
    private final PlaylistAnnotationService playlistAnnotationService;

    public TimestampAnnotationService(TimestampService timestampService,
            TrackAnnotationService trackAnnotationService,
            PlaylistAnnotationService playlistAnnotationService) {
        this.timestampService = timestampService;
        this.trackAnnotationService = trackAnnotationService;
        this.playlistAnnotationService = playlistAnnotationService;

    }

    public Timestamp addTimestampNote(String userID, String playlistID, String trackID, Integer trackNumber,
            String note, Long timestampID) throws PlaylistNotFoundException, TrackNotFoundException,
            UserNotFoundException, TimestampNotFoundException {

        try {
            PlaylistMember trackInPlaylist = trackAnnotationService.getPlaylistTrackFromDB(userID, playlistID, trackID,
                    trackNumber);

            Timestamp timestamp = timestampService
                    .findByTimestampIDAndPlaylistMemberId(timestampID, trackInPlaylist.getPlaylistMemberID())
                    .orElseThrow(TimestampNotFoundException::new);

            timestamp.setNote(note);
            return timestampService.save(timestamp);

        } catch (PlaylistNotFoundException e) {
            playlistAnnotationService.saveNewPlaylistEntity(userID, playlistID);
        } catch (TrackNotFoundException e) {
            trackAnnotationService.saveNewPlaylistTrackEntity(userID, playlistID, trackID, trackNumber);
        }

        return addTimestampNote(userID, playlistID, trackID, trackNumber, note, timestampID);

    }

    /**
     * Update an existing timestamp note for a track in a playlist. For ease of use, timestamp
     * notes are retrieved by timestamp ID, as opposed to interval start and end
     * time.
     * 
     */
    public Timestamp updateTimestampNote(String userID, Long timestampID,
            String note) throws TimestampNotFoundException {

        Timestamp timestamp = timestampService.findByTimestampIDAndUserID(timestampID, userID)
                .orElseThrow(TimestampNotFoundException::new); // the timestamp with provided ID does not exist in the
                                                               // DB or does not belong to the user
        timestamp.setNote(note);
        return timestampService.save(timestamp);
    }

}

// /**
// * TODO: check thisss
// *
// * @param userID
// * @param playlistID
// * @param trackID
// * @param trackNumber
// * @return
// * @throws TrackNotFoundException
// * @throws PlaylistNotFoundException
// */
// public TimestampResponse getTimestampNotes(String userID, String playlistID,
// String trackID, Integer trackNumber)
// throws TrackNotFoundException, PlaylistNotFoundException,
// UserNotFoundException {
// PlaylistMember trackInPlaylist;
// try {
// trackInPlaylist = getTrackFromDB(userID, playlistID, trackID, trackNumber);
// ArrayList<TimestampDTO> timestampDTOs =
// trackInPlaylist.getTimestamps().stream().map((ts) -> {
// return new TimestampDTO(
// ts.getId(),
// String.format("%d:%02d", ts.getStart().toMinutesPart(),
// ts.getStart().toSecondsPart()),
// String.format("%d:%02d", ts.getEnd().toMinutesPart(),
// ts.getEnd().toSecondsPart()),
// ts.getNote());
// }).collect(Collectors.toCollection(ArrayList::new));
// return new TimestampResponse(timestampDTOs);

// } catch (PlaylistNotFoundException e) {
// if (playlistExists(playlistID, userID)) {
// return new TimestampResponse(new ArrayList<>());
// } else {
// throw new PlaylistNotFoundException();
// }
// } catch (TrackNotFoundException e) {
// if (trackExistsInPlaylist(playlistID, userID, trackID, trackNumber)) {
// return new TimestampResponse(new ArrayList<>());
// } else {
// throw new TrackNotFoundException();
// }
// }
// }

// public Timestamp addTimestampNote(String userID, String playlistID, String
// trackID, Integer trackNumber,
// String startInMins, String endInMins, String note)
// throws PlaylistNotFoundException, TrackNotFoundException,
// UserNotFoundException {

// PlaylistMember trackInPlaylist = null;

// try {
// trackInPlaylist = getTrackFromDB(userID, playlistID, trackID, trackNumber);
// return createNewTimestamp(startInMins, endInMins, note, trackInPlaylist,
// trackID);
// } catch (PlaylistNotFoundException e) {
// if (playlistExists(playlistID, userID)) {
// Playlist playlist = createNewPlaylistEntity(userID, playlistID);
// playlistService.save(playlist);
// return addTimestampNote(userID, playlistID, trackID, trackNumber,
// startInMins, endInMins, note);
// } else {
// throw new PlaylistNotFoundException();
// }
// } catch (TrackNotFoundException e) {
// if (trackExistsInPlaylist(playlistID, userID, trackID, trackNumber)) {
// Track track = createNewTrackEntity(trackID, trackNumber);
// Playlist playlist = getPlaylistFromDB(userID, playlistID);
// createTrackPlaylistRelationship(track, playlist, trackNumber);
// return addTimestampNote(userID, playlistID, trackID, trackNumber,
// startInMins, endInMins, note);
// } else {
// throw new TrackNotFoundException();
// }
// }
// }

// public Timestamp updateTimestampNote(String userID, String playlistID, String
// trackID, Integer trackNumber,
// String note, Long timestampID) throws PlaylistNotFoundException,
// TrackNotFoundException,
// UserNotFoundException, TimestampNotFoundException {

// PlaylistMember trackInPlaylist = null;

// try {
// trackInPlaylist = getTrackFromDB(userID, playlistID, trackID, trackNumber);
// Optional<Timestamp> timestampOptional =
// timestampRepository.findByIdAndPlaylistMember(timestampID,
// trackInPlaylist);
// if (timestampOptional.isEmpty()) {
// throw new TimestampNotFoundException();
// }
// Timestamp timestamp = timestampOptional.get();
// timestamp.setNote(note);
// return timestampRepository.save(timestamp);

// } catch (PlaylistNotFoundException e) {
// if (playlistExists(playlistID, userID)) {
// Playlist playlist = createNewPlaylistEntity(userID, playlistID);
// playlistService.save(playlist);
// return updateTimestampNote(userID, playlistID, trackID, trackNumber, note,
// timestampID);
// } else {
// throw new PlaylistNotFoundException();
// }
// } catch (TrackNotFoundException e) {
// if (trackExistsInPlaylist(playlistID, userID, trackID, trackNumber)) {
// Track track = createNewTrackEntity(trackID, trackNumber);
// Playlist playlist = getPlaylistFromDB(userID, playlistID);
// createTrackPlaylistRelationship(track, playlist, trackNumber);
// return updateTimestampNote(userID, playlistID, trackID, trackNumber, note,
// timestampID);
// } else {
// throw new TrackNotFoundException();
// }
// } catch (TimestampNotFoundException e) {
// throw new TimestampNotFoundException();
// }
// }

// public void deleteTimestampNote(String userID, String playlistID, String
// trackID, Integer trackNumber,
// Long timestampID) throws PlaylistNotFoundException, TrackNotFoundException,
// UserNotFoundException,
// TimestampNotFoundException {

// PlaylistMember trackInPlaylist = getTrackFromDB(userID, playlistID, trackID,
// trackNumber);
// Optional<Timestamp> timestampOptional =
// timestampRepository.findByIdAndPlaylistMember(timestampID,
// trackInPlaylist);
// if (timestampOptional.isEmpty()) {
// throw new TimestampNotFoundException();
// }
// Timestamp timestamp = timestampOptional.get();
// timestampRepository.delete(timestamp);
// }

// private Timestamp createNewTimestamp(String startInMins, String endInMins,
// String note,
// PlaylistMember playlistMember,
// String trackID) {
// TrackInfo trackInfo = getTrackDetailsFromSpotify(trackID);
// startInMins = startInMins.trim();
// endInMins = endInMins.trim();
// Pattern pattern = Pattern.compile("^(\\d{1,2}):(\\d{2})$");
// Matcher startMatcher = pattern.matcher(startInMins);
// Matcher endMatcher = pattern.matcher(endInMins);

// if (!startMatcher.find()) {
// throw new IllegalArgumentException("Start time must be in the format mm:ss");
// }

// if (!endMatcher.find()) {
// throw new IllegalArgumentException("End time must be in the format mm:ss");
// }

// Duration startDuration =
// Duration.ofMinutes(Long.parseLong(startMatcher.group(1)))
// .plusSeconds(Long.parseLong(startMatcher.group(2)));
// Duration endDuration =
// Duration.ofMinutes(Long.parseLong(endMatcher.group(1)))
// .plusSeconds(Long.parseLong(endMatcher.group(2)));
// Duration trackDuration = Duration.ofMillis(trackInfo.duration_ms());

// if (startDuration.isNegative() || endDuration.isNegative()) {
// throw new IllegalArgumentException("Start time and end time must be
// non-negative");
// }
// if (startDuration.compareTo(endDuration) > 0) {
// throw new IllegalArgumentException("Start time must be less than end time");
// }

// if (endDuration.compareTo(trackDuration) >= 0) {
// throw new IllegalArgumentException("End time must be less than track
// duration");
// }

// Timestamp timestamp = new Timestamp(startDuration, endDuration, note,
// playlistMember);
// return timestampRepository.save(timestamp);
// }
// }

package com.nandincube.jamjot.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nandincube.jamjot.dto.TimestampDTO;
import com.nandincube.jamjot.dto.TimestampResponse;
import com.nandincube.jamjot.exceptions.PlaylistNotFoundException;
import com.nandincube.jamjot.exceptions.TimestampNotFoundException;
import com.nandincube.jamjot.exceptions.TrackNotFoundException;
import com.nandincube.jamjot.exceptions.UserNotFoundException;
import com.nandincube.jamjot.model.PlaylistMember;
import com.nandincube.jamjot.model.Timestamp;
import com.nandincube.jamjot.model.Track;

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

    /**
     * Save a new timestamp note for a track in a playlist.
     * 
     * @param intervalStart  - the start time of the timestamp interval, in the
     *                       format mm:ss
     * @param intervalEnd    - the end time of the timestamp interval, in the format
     *                       mm:ss
     * @param note           - the content of the timestamp note
     * @param playlistMember - the PlaylistMember entity representing the track in
     *                       the playlist that the timestamp note belongs to
     * @return the created Timestamp object
     * @throws TrackNotFoundException - if the track associated with the
     *                                playlistMember does not exist in the DB
     */
    private Timestamp saveNewTimestamp(String intervalStart, String intervalEnd,
            String note,
            PlaylistMember playlistMember) throws TrackNotFoundException {

        Track track = playlistMember.getTrack();
        if (track == null)
            throw new TrackNotFoundException();

        Duration start = convertMinsAsStringToDuration(intervalStart);
        Duration end = convertMinsAsStringToDuration(intervalEnd);
        Duration trackDuration = track.getDuration();

        validateInterval(start, end, trackDuration);

        Timestamp timestamp = new Timestamp(start, end, note, playlistMember);

        return timestampService.save(timestamp);
    }

    /**
     * Add a timestamp note for a track in a playlist.
     * 
     * @param userID        - the authenticated user's ID
     * @param playlistID    - the Spotify ID of the playlist
     * @param trackID       - the Spotify ID of the track
     * @param trackNumber   - position of the track in the playlist (used to
     *                      disambiguate between multiple instances of the same
     *                      track in a playlist)
     * @param note          - the content of the timestamp note
     * @param intervalStart - the start time of the timestamp interval, in the
     *                      format mm:ss
     * @param intervalEnd   - the end time of the timestamp interval, in the format
     *                      mm:ss
     * @return the created Timestamp object
     * @throws PlaylistNotFoundException - if the playlist does not exist in the DB
     *                                   or does not belong to the user
     * @throws TrackNotFoundException    - if the track does not exist in the DB or
     *                                   does not belong to the playlist
     * @throws UserNotFoundException     - if the user does not exist in the DB
     * 
     */
    public Timestamp addTimestampNote(String userID, String playlistID, String trackID, Integer trackNumber,
            String note, String intervalStart, String intervalEnd)
            throws PlaylistNotFoundException, TrackNotFoundException,
            UserNotFoundException {

        try {
            PlaylistMember trackInPlaylist = trackAnnotationService.getPlaylistTrackFromDB(userID, playlistID, trackID,
                    trackNumber);
            return saveNewTimestamp(intervalStart, intervalEnd, note, trackInPlaylist);

        } catch (PlaylistNotFoundException e) {
            playlistAnnotationService.saveNewPlaylistEntity(userID, playlistID);
        } catch (TrackNotFoundException e) {
            trackAnnotationService.saveNewPlaylistTrackEntity(userID, playlistID, trackID, trackNumber);
        }

        return addTimestampNote(userID, playlistID, trackID, trackNumber, note, intervalStart, intervalEnd);

    }

    /**
     * Update the note for a specific timestamp.
     * 
     * @param userID      - ID of the authenticated user.
     * @param timestampID - ID of the timestamp to update.
     * @param note        - The new note content to set for the timestamp.
     * @return The updated Timestamp object after saving the new note.
     * @throws TimestampNotFoundException - If the timestamp with the provided ID
     *                                    does not exist in the DB or does not
     *                                    belong to the user.
     */
    public Timestamp updateTimestampNote(String userID, Long timestampID,
            String note) throws TimestampNotFoundException {

        Timestamp timestamp = timestampService.findByTimestampIDAndUserID(timestampID, userID)
                .orElseThrow(TimestampNotFoundException::new); // the timestamp with provided ID does not exist in the
                                                               // DB or does not belong to the user
        timestamp.setNote(note);
        return timestampService.save(timestamp);
    }

    /**
     * Convert a time string in the format mm:ss to a Duration object.
     * 
     * @param timeInMins - the time string to convert, in the format mm:ss
     * @return a Duration object representing the input time
     * @throws IllegalArgumentException if the input string is not in the correct
     *                                  format
     */
    private Duration convertMinsAsStringToDuration(String timeInMins) {
        timeInMins = timeInMins.trim();
        Pattern pattern = Pattern.compile("^(\\d{1,2}):(\\d{2})$");
        Matcher matcher = pattern.matcher(timeInMins);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Error: Time must be in the format mm:ss");
        }

        return Duration.ofMinutes(Long.parseLong(matcher.group(1)))
                .plusSeconds(Long.parseLong(matcher.group(2)));
    }

    /**
     * Validate a timestamp interval by checking that the start and end times are in
     * the correct format, that they are non-negative, that the start time is less
     * than the end time, and that the end time is less than the track duration.
     * 
     * @param start         - the start time of the timestamp interval
     * @param end           - the end time of the timestamp interval
     * @param trackDuration - the duration of the track
     * @throws IllegalArgumentException if any of the validation checks fail
     */
    private void validateInterval(Duration start, Duration end, Duration trackDuration) {
        if (start.isNegative() || end.isNegative()) {
            throw new IllegalArgumentException("Error: Start time and end time must be non-negative");
        }
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Error: Start time must be less than end time");
        }
        if (end.compareTo(trackDuration) > 0) {
            throw new IllegalArgumentException("Error: End time must be less than track duration");
        }
    }

    /**
     * Get all timestamp notes for a track in a playlist.
     * 
     * @param userID      - the authenticated user's ID
     * @param playlistID  - the Spotify ID of the playlist
     * @param trackID     - the Spotify ID of the track
     * @param trackNumber - position of the track in the playlist (used to
     *                    disambiguate between multiple instances of the same track
     *                    in a playlist)
     * @return a TimestampResponse object containing a list of TimestampDTOs
     *         representing the timestamp notes for the track in the playlist
     * @throws TrackNotFoundException - if the track does not exist in the DB or
     *                                does not belong to the playlist
     */
    public TimestampResponse getTimestampNotes(String userID, String playlistID,
            String trackID, Integer trackNumber) throws PlaylistNotFoundException, TrackNotFoundException

    {

        ArrayList<Timestamp> timestamps = timestampService.findByPlaylistMemberID(userID, playlistID, trackID,
                trackNumber);
        if (!timestamps.isEmpty()) {
            ArrayList<TimestampDTO> timestampDTOs = (ArrayList<TimestampDTO>) timestamps
                    .stream()
                    .map((ts) -> createTimestampDTO(ts))
                    .collect(Collectors.toList());

            return new TimestampResponse(timestampDTOs);
        }

        if (!trackAnnotationService.playlistTrackExistsOnSpotify(playlistID, userID, trackID, trackNumber)) {
            throw new TrackNotFoundException();
        }

        return new TimestampResponse(new ArrayList<TimestampDTO>());
    }

    /**
     * This method creates a TimestampDTO object from a Timestamp entity. It formats
     * the start and end times of the timestamp as strings in the format mm:ss.
     * 
     * @param timestamp - the Timestamp entity to convert
     * @return a TimestampDTO object representing the input Timestamp entity
     */
    private TimestampDTO createTimestampDTO(Timestamp timestamp) {
        return new TimestampDTO(
                timestamp.getId(),
                String.format("%d:%02d", timestamp.getStart().toMinutes(), timestamp.getStart().toSeconds()),
                String.format("%d:%02d", timestamp.getEnd().toMinutes(), timestamp.getEnd().toSeconds()),
                timestamp.getNote());
    }

    /**
     * This method deletes a timestamp note for a track in a playlist.
     * 
     * @param userID      - ID of the authenticated user.
     * @param timestampID - ID of the timestamp to delete.
     * @throws TimestampNotFoundException - If the timestamp with the provided ID
     *                                    does not exist in the DB or does not
     *                                    belong to the user.
     */
    public void deleteTimestampNote(String userID, Long timestampID) throws TimestampNotFoundException {

        Timestamp timestamp = timestampService.findByTimestampIDAndUserID(timestampID, userID)
                .orElseThrow(TimestampNotFoundException::new); // the timestamp with provided ID does not exist in the
                                                               // DB or does not belong to the user
        timestampService.delete(timestamp);
    }

}

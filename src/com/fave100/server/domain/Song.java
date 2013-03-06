package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fave100.shared.SongInterface;
import com.google.appengine.api.rdbms.AppEngineDriver;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;

@Entity
public class Song extends DatastoreObject implements SongInterface {

	// TODO: Any risks of using token in ID like this?
	@IgnoreSave public static final String TOKEN_SEPARATOR = ":%:";
	@IgnoreSave public static String YOUTUBE_API_KEY = "";

	@Id private String id;
	private String artist;
	private String song;
	private String coverArtUrl;

	@SuppressWarnings("unused")
	private Song(){}

	// TODO: id should be passed in from SQL not generated from name + song
	public Song(final String name, final String artist, final String id) {
		this.song = name;
		this.artist = artist;
		this.id = name + Song.TOKEN_SEPARATOR + artist;
	}

	public static Song findSong(final String id) {
		return ofy().load().type(Song.class).id(id).get();
	}

	public static String createSongId(final String song, final String artist) {
		return song + Song.TOKEN_SEPARATOR + artist;
	}

	public static Song findSongByTitleAndArtist(final String songTitle,
		final String artist) {

		// Try to find the song
		final String id = songTitle + Song.TOKEN_SEPARATOR + artist;
		Song song = ofy().load().type(Song.class).id(id).get();
		if(song != null) {
			return song;
		} else {
			// Look up the song in the SQL database and add to AppEngine datastore
			Connection connection = null;
			try {
				// Make connection
				DriverManager.registerDriver(new AppEngineDriver());
				connection = DriverManager.getConnection("jdbc:google:rdbms://caseware.com:fave100:fave100dev/testgoogle");
				// Make SQL query
				String statement = "SELECT song, artist, mbid, youtube_id FROM autocomplete_search WHERE ";
				statement += "searchable_song = LOWER(?) AND song = (?) AND artist = (?) LIMIT 1";
				final PreparedStatement stmt = connection.prepareStatement(statement);
				stmt.setString(1, songTitle);
				stmt.setString(2, songTitle);
				stmt.setString(3, artist);
				final ResultSet results = stmt.executeQuery();
				// Turn results into Song and save
				if(results.next()) {
					song = new Song(results.getString("song"), results.getString("artist"), results.getString("mbid"));
				    ofy().save().entity(song).now();
				}
				stmt.close();
			} catch (final SQLException ignore) {
			} finally {
				if (connection != null) {
					try {

						connection.close();
					} catch (final SQLException ignore) {
					}
				}
			}
		}

		return song;
	}

	public static String getYouTubeResults(final String song, final String artist) {
		try {
			String searchUrl = "https://www.googleapis.com/youtube/v3/search?part=id%2C+snippet&maxResults=5&type=video";
			searchUrl += "&q="+song.replace(" ", "+")+"+"+artist.replace(" ", "+");
			searchUrl += "&key="+Song.YOUTUBE_API_KEY;
		    final URL url = new URL(searchUrl);
		    final URLConnection conn = url.openConnection();
		    final BufferedReader in = new BufferedReader(new InputStreamReader(
	    		conn.getInputStream(), "UTF-8"));

			String inputLine;
			String content = "";

			while ((inputLine = in.readLine()) != null) {
			    content += inputLine;
			}
			in.close();

			return content;
		} catch (final Exception e) {

		}
		return null;
	}

	public static Song addSong(final String song, final String artist) {
		final Song newSong = null;


		return newSong;
	}

	@Override
	public boolean equals(final Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;
        if(((Song) obj).getId().equals(this.getId()))
        	return true;
        return false;
	}

	/* Getters and setters */

	@Override
	public String getArtist() {
		return artist;
	}

	public void setArtist(final String artist) {
		this.artist = artist;
	}

	@Override
	public String getSong() {
		return this.song;
	}

	public void setSong(final String song) {
		this.song = song;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	@Override
	public String getCoverArtUrl() {
		return coverArtUrl;
	}

	public void setCoverArtUrl(final String coverArtUrl) {
		this.coverArtUrl = coverArtUrl;
	}

}

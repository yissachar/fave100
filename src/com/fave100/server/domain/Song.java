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
import java.util.ArrayList;
import java.util.List;

import com.fave100.shared.SongInterface;
import com.google.appengine.api.rdbms.AppEngineDriver;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.OnLoad;

@Entity
public class Song extends DatastoreObject implements SongInterface {

	// TODO: Any risks of using token in ID like this?
	@IgnoreSave public static final String TOKEN_SEPARATOR = ":%:";
	@IgnoreSave public static String YOUTUBE_API_KEY = "";

	@Id private String id;
	private long score = 0;
	private String mbid;
	private String artist;
	private String title;
	private String coverArtUrl;
	private String releaseDate;
	@IgnoreSave private String whyline;
	@IgnoreSave private int whylineScore;
	@IgnoreSave private int resultCount;

	//TODO: Need to periodically update cache?

	@SuppressWarnings("unused")
	private Song(){}

	public Song(final String title, final String artist, final String mbid) {
		this.title = title;
		this.artist = artist;
		this.mbid = mbid;
		this.id = title + Song.TOKEN_SEPARATOR + artist;
	}

	public static Song findSong(final String id) {
		return ofy().load().type(Song.class).id(id).get();
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

	public void addScore(final int score) {
		this.score += score;
	}

	@OnLoad
	@SuppressWarnings("unused")
	private void onLoad(final Objectify ofy) {
		final List<Whyline> list =  ofy.load().type(Whyline.class)
										.filter("song", Ref.create(Key.create(Song.class, getId())))
										.order("score")
										.limit(1)
										.list();
		if(list.size() > 0) {
			whyline = list.get(0).getWhyline();
		} else {
			whyline = "";
		}

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

	public static List<Song> getAutocomplete(final String songTerm) {
		Connection connection = null;
		final List<Song> autocompleteList = new ArrayList<Song>();
		try {
			// Make connection
			DriverManager.registerDriver(new AppEngineDriver());
			connection = DriverManager.getConnection("jdbc:google:rdbms://caseware.com:fave100:fave100dev/testgoogle");
			// Make SQL query
			String statement = "SELECT song, artist, mbid FROM autocomplete_search WHERE searchable_song ";
			statement += "LIKE LOWER(?) ORDER BY rank DESC LIMIT 5";
			final PreparedStatement stmt = connection.prepareStatement(statement);
			stmt.setString(1, songTerm+"%");
			final ResultSet results = stmt.executeQuery();
			// Turn results into ArrayList
			while(results.next()) {
			//	final Song song = new Song(results.getString("song"), results.getString("artist"), results.getString("mbid"));
			//	autocompleteList.add(song);
			}
		} catch (final SQLException ignore) {
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (final SQLException ignore) {
				}
			}
		}
		return autocompleteList;
	}

	public static List<Song> searchSong(final String song, final int offset) {
		return search(song, "", offset);
	}

	public static List<Song> searchArtist(final String artist, final int offset) {
		return search("", artist, offset);
	}

	public static List<Song> search(final String song, final String artist, final int offset) {
		final int limit = 25;

		if(song.isEmpty() && artist.isEmpty()) return null;

		String baseQuery = "";
		String resultQuery = "SELECT song, artist, mbid FROM autocomplete_search WHERE ";
		String countQuery = "SELECT COUNT(*) AS count FROM autocomplete_search WHERE ";
		if(!song.isEmpty()) {
			baseQuery += "MATCH(searchable_song) AGAINST (LOWER(?) IN BOOLEAN MODE) ";
		}
		if(!artist.isEmpty()) {
			if(!song.isEmpty()) baseQuery += " AND ";
			baseQuery += "MATCH(searchable_artist) AGAINST (LOWER(?) IN BOOLEAN MODE) ";
		}

		resultQuery += baseQuery;
		resultQuery += "ORDER BY rank DESC LIMIT ? OFFSET ?";

		countQuery += baseQuery;

		String query = "SELECT * FROM (";
		query += resultQuery + ") AS result CROSS JOIN (";
		query += countQuery + ") AS count;";

		final List<Song> searchResults = new ArrayList<Song>();
		Connection connection = null;
		try {
			// Make connection
			DriverManager.registerDriver(new AppEngineDriver());
			connection = DriverManager.getConnection("jdbc:google:rdbms://caseware.com:fave100:fave100dev/testgoogle");
			// Make SQL query
			final PreparedStatement stmt = connection.prepareStatement(query);
			int pos = 1;
			// Set params for result query
			if(!song.isEmpty()){
				stmt.setString(pos, song);
				pos++;
			}
			if(!artist.isEmpty()){
				stmt.setString(pos, artist);
				pos++;
			}
			stmt.setInt(pos, limit);
			pos++;
			stmt.setInt(pos, offset);
			pos++;
			// Set params for count query
			if(!song.isEmpty()){
				stmt.setString(pos, song);
				pos++;
			}
			if(!artist.isEmpty()){
				stmt.setString(pos, artist);
				pos++;
			}
			final ResultSet results = stmt.executeQuery();
			// Turn results into ArrayList
			while(results.next()) {
				final Song songResult = new Song(results.getString("song"), results.getString("artist"), results.getString("mbid"));
				songResult.setResultCount(results.getInt("count"));
				searchResults.add(songResult);
			}
		} catch (final SQLException ignore) {
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (final SQLException ignore) {
				}
			}
		}
		return searchResults;
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
	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(final String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public long getScore() {
		return score;
	}

	public void setScore(final long score) {
		this.score = score;
	}

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

	public String getWhyline() {
		return whyline;
	}

	public void setWhyline(final String whyline) {
		this.whyline = whyline;
	}

	public int getWhylineScore() {
		return whylineScore;
	}

	public void setWhylineScore(final int whylineScore) {
		this.whylineScore = whylineScore;
	}

	@Override
	public String getMbid() {
		return mbid;
	}

	public void setMbid(final String mbid) {
		this.mbid = mbid;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(final int resultCount) {
		this.resultCount = resultCount;
	}

}

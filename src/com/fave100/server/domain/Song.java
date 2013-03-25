package com.fave100.server.domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fave100.shared.Constants;
import com.fave100.shared.SongInterface;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;

/**
 * Represents a Song that users can add to their lists. This Song will not
 * actually be persisted directly in the datastore. Instead we will lookup a
 * Song from Lucene API and then store a denormalized embedded FaveItem
 * representing the Song.
 *
 * @author yissachar.radcliffe
 *
 */
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
		this.id = id;
	}

	public static Song findSong(final String id) {
		try {Logger.getAnonymousLogger().log(Level.SEVERE, "starting");
			final String lookupUrl = Constants.LOOKUP_URL+"id="+id;
		    final URL url = new URL(lookupUrl);Logger.getAnonymousLogger().log(Level.SEVERE, "url is: "+lookupUrl);
		    final URLConnection conn = url.openConnection();
		    final BufferedReader in = new BufferedReader(new InputStreamReader(
	    		conn.getInputStream(), "UTF-8"));

			String inputLine;
			String content = "";

			while ((inputLine = in.readLine()) != null) {
			    content += inputLine;
			}
			in.close();
Logger.getAnonymousLogger().log(Level.SEVERE, "Result is: "+content);
			final JsonParser parser = new JsonParser();
		    final JsonElement jsonElement = parser.parse(content);
		    final JsonObject jsonSong = jsonElement.getAsJsonObject();
		    final Song song = new Song(jsonSong.get("song").getAsString(), jsonSong.get("artist").getAsString(), id);
		    return song;
		} catch (final Exception e) {
			// TODO: Catch error
		}
		return null;
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

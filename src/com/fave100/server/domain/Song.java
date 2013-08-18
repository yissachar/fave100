package com.fave100.server.domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.fave100.shared.Constants;
import com.fave100.shared.SongInterface;
import com.google.gson.JsonArray;
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

	@IgnoreSave private static String YOUTUBE_API_KEY = "";

	@Id private String id;
	private String artist;
	private String song;
	private String coverArtUrl;

	public Song() {
	}

	public Song(final String name, final String artist, final String id) {
		this.song = name;
		this.artist = artist;
		this.id = id;
	}

	public static Song findSong(final String id) throws Exception {
		try {
			final String lookupUrl = Constants.LOOKUP_URL + "id=" + id;
			final URL url = new URL(lookupUrl);
			final URLConnection conn = url.openConnection();
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));

			String inputLine;
			String content = "";

			while ((inputLine = in.readLine()) != null) {
				content += inputLine;
			}
			in.close();

			final JsonParser parser = new JsonParser();
			final JsonElement jsonElement = parser.parse(content);
			final JsonObject jsonSong = jsonElement.getAsJsonObject();
			final Song song = new Song(jsonSong.get("song").getAsString(), jsonSong.get("artist").getAsString(), id);
			return song;
		}
		catch (final Exception e) {
			throw (e);
		}
	}

	public static List<YouTubeSearchResult> getYouTubeResults(final String song, final String artist) {
		try {
			String searchUrl = "https://www.googleapis.com/youtube/v3/search?part=id%2C+snippet&maxResults=5&type=video&videoEmbeddable=true";
			searchUrl += "&q=" + song.replace(" ", "+") + "+" + artist.replace(" ", "+");
			searchUrl += "&key=" + Song.YOUTUBE_API_KEY;
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

			final List<YouTubeSearchResult> youtubeResults = new ArrayList<YouTubeSearchResult>();
			final JsonParser parser = new JsonParser();
			final JsonElement resultsElement = parser.parse(content);
			final JsonObject resultsObject = resultsElement.getAsJsonObject();
			final JsonArray items = resultsObject.get("items").getAsJsonArray();
			for (int i = 0; i < items.size(); i++) {
				final JsonObject item = items.get(i).getAsJsonObject();

				final String videoId = item.get("id").getAsJsonObject().get("videoId").getAsString();
				final String thumbnail = item.get("snippet").getAsJsonObject()
						.get("thumbnails").getAsJsonObject()
						.get("default").getAsJsonObject()
						.get("url").getAsString();
				youtubeResults.add(new YouTubeSearchResult(videoId, thumbnail));

			}
			return youtubeResults;
		}
		catch (final Exception e) {

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
		if (((Song)obj).getId().equals(this.getId()))
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

	public static void setYoutubeApiKey(final String key) {
		YOUTUBE_API_KEY = key;
	}

}

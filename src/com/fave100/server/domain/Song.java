package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;

@Entity
public class Song extends DatastoreObject {

	@IgnoreSave public static final String TOKEN_SEPARATOR = ":%:";
	@IgnoreSave public static String YOUTUBE_API_KEY = "";

	@Id private String id;
	@Index private long score = 0;
	private String mbid;
	private String artistName;
	private String trackName;
	private String youTubeId;
	private String trackViewUrl;
	private String coverArtUrl;
	private String releaseDate;
	private String primaryGenreName;
	@IgnoreSave private String whyline;
	@IgnoreSave private int whylineScore;

	//TODO: Need to periodically update cache?

	@SuppressWarnings("unused")
	private Song(){}

	public Song(final String songTitle, final String artist, final String mbid) {
		this.trackName = songTitle;
		this.artistName = artist;
		this.mbid = mbid;
		this.id = songTitle + Song.TOKEN_SEPARATOR + artist;
	}

	public static Song findSong(final String id) {
		return ofy().load().type(Song.class).id(id).get();
	}

	public static Song findSongByTitleAndArtist(final String songTitle,
		final String artist) {

		final String id = songTitle + Song.TOKEN_SEPARATOR + artist;
		return ofy().load().type(Song.class).id(id).get();
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

	/* Getters and setters */

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(final String artistName) {
		this.artistName = artistName;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(final String trackName) {
		this.trackName = trackName;
	}

	public String getTrackViewUrl() {
		return trackViewUrl;
	}

	public void setTrackViewUrl(final String trackViewUrl) {
		this.trackViewUrl = trackViewUrl;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(final String releaseDate) {
		this.releaseDate = releaseDate;
	}


	public String getPrimaryGenreName() {
		return primaryGenreName;
	}

	public void setPrimaryGenreName(final String primaryGenreName) {
		this.primaryGenreName = primaryGenreName;
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

	public String getMbid() {
		return id;
	}

	public void setMbid(final String mbid) {
		this.mbid = mbid;
	}

	public String getYouTubeId() {
		return youTubeId;
	}

	public void setYouTubeId(final String youTubeId) {
		this.youTubeId = youTubeId;
	}

	public String getYouTubeEmbedUrl() {
		if(getYouTubeId() != null && !getYouTubeId().isEmpty()) {
			return "http://www.youtube.com/embed/"+getYouTubeId();
		}
		return null;
	}

}

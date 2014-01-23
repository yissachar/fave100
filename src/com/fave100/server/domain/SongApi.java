package com.fave100.server.domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.shared.Constants;
import com.google.api.server.spi.config.ApiMethod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SongApi extends ApiBase {

	@ApiMethod(name = "song.getSong", path = "song")
	public FaveItem getSong(@Named("id") final String id) {
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
			final FaveItem song = new FaveItem(jsonSong.get("song").getAsString(), jsonSong.get("artist").getAsString(), id);
			return song;
		}
		catch (final Exception e) {
			// TODO: Tell client that we failed
		}
		return null;
	}

	@ApiMethod(name = "song.getYouTubeSearchResults", path = "song/search")
	public List<YouTubeSearchResult> getYouTubeResults(@Named("song") final String song, @Named("artist") final String artist) {
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
}

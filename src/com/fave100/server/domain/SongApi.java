package com.fave100.server.domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.inject.Named;

import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.shared.Constants;
import com.google.api.server.spi.config.ApiMethod;
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
}

package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.Song;
import com.fave100.server.domain.UserListResult;
import com.fave100.server.domain.UserListResultCollection;
import com.fave100.server.domain.YouTubeSearchResult;
import com.fave100.server.domain.YouTubeSearchResultCollection;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/" + ApiPaths.SONG_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.SONG_ROOT, description = "Operations on Songs")
public class SongApi {

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Find a song by ID", response = FaveItem.class)
	public static FaveItem getSong(@ApiParam(value = "ID of song to be fetched", required = true) @PathParam("id") final String id) {
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
			try {
				final JsonObject jsonSong = jsonElement.getAsJsonObject();
				final FaveItem song = new FaveItem(jsonSong.get("song").getAsString(), jsonSong.get("artist").getAsString(), id);
				return song;
			}
			catch (final IllegalStateException e) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
		}
		catch (final IOException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path(ApiPaths.GET_YOUTUBE_SEARCH_RESULTS)
	@ApiOperation(value = "Find YouTube videos for a song", response = YouTubeSearchResultCollection.class)
	public static YouTubeSearchResultCollection getYouTubeResults(
			@ApiParam(value = "The song title", required = true) @QueryParam("song") final String song,
			@ApiParam(value = "The song artist", required = true) @QueryParam("artist") final String artist) {

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
			return new YouTubeSearchResultCollection(youtubeResults);
		}
		catch (final IOException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/{id}/favelists")
	@ApiOperation(value = "Get a list of users who have this song in their FaveList", response = UserListResultCollection.class)
	public static UserListResultCollection getFaveLists(@ApiParam(value = "The song ID", required = true) @PathParam("id") final String id) {
		final List<UserListResult> userListResults = new ArrayList<>();

		// Get up to 30 FaveLists containing the song
		final List<FaveList> faveLists = ofy().load().type(FaveList.class).filter("list.songID", id).limit(30).list();

		// Get the user's avatars
		for (final FaveList faveList : faveLists) {
			ofy().load().ref(faveList.getUser());
			final AppUser user = faveList.getUser().get();
			String avatar = "";
			if (user != null)
				avatar = user.getAvatarImage(30);

			UserListResult userListResult = new UserListResult(user.getUsername(), faveList.getHashtag(), avatar);
			userListResults.add(userListResult);
		}
		return new UserListResultCollection(userListResults);
	}
}

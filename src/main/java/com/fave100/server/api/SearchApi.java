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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.CursoredSearchResult;
import com.fave100.server.domain.Song;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.StringResultCollection;
import com.fave100.server.domain.YouTubeSearchResult;
import com.fave100.server.domain.YouTubeSearchResultCollection;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.favelist.Hashtag;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.objectify.cmd.Query;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("/" + ApiPaths.SEARCH_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.SEARCH_ROOT, description = "Search operations")
public class SearchApi {

	@GET
	@Path(ApiPaths.GET_YOUTUBE_SEARCH_RESULTS)
	@ApiOperation(value = "Find YouTube videos for a song", response = YouTubeSearchResultCollection.class)
	public static YouTubeSearchResultCollection getYouTubeResults(@QueryParam(ApiPaths.YOUTUBE_SEARCH_SONG_PARAM) final String song,
			@QueryParam(ApiPaths.YOUTUBE_SEARCH_ARTIST_PARAM) final String artist) {

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
	@Path(ApiPaths.SEARCH_FAVELISTS)
	@ApiOperation(value = "Search for FaveLists", response = CursoredSearchResult.class)
	public static CursoredSearchResult searchFaveLists(@QueryParam("search_term") final String searchTerm, @QueryParam("cursor") final String cursor) {

		final List<StringResult> names = new ArrayList<>();

		if (searchTerm.isEmpty())
			new CursoredSearchResult(null, new StringResultCollection(names));

		// TODO: Need to sort by popularity
		Query<Hashtag> query = ofy().load().type(Hashtag.class).filter("id >=", searchTerm.toLowerCase()).filter("id <", searchTerm.toLowerCase() + "\uFFFD").limit(5);
		if (cursor != null) {
			query = query.startAt(Cursor.fromWebSafeString(cursor));
		}

		final QueryResultIterator<Hashtag> iterator = query.iterator();
		while (iterator.hasNext()) {
			Hashtag hashtag = iterator.next();
			names.add(new StringResult(hashtag.getName()));
		}

		return new CursoredSearchResult(iterator.getCursor().toWebSafeString(), new StringResultCollection(names));
	}

	@GET
	@Path(ApiPaths.SEARCH_USERS)
	@ApiOperation(value = "Search for Users", response = CursoredSearchResult.class)
	public static CursoredSearchResult searchUsers(@QueryParam("search_term") final String searchTerm, @QueryParam("cursor") final String cursor) {

		final List<StringResult> names = new ArrayList<>();

		if (searchTerm.isEmpty())
			new CursoredSearchResult(null, new StringResultCollection(names));

		Query<AppUser> query = ofy().load().type(AppUser.class).filter("usernameID >=", searchTerm.toLowerCase()).filter("usernameID <", searchTerm.toLowerCase() + "\uFFFD").limit(5);
		if (cursor != null) {
			query = query.startAt(Cursor.fromWebSafeString(cursor));
		}

		final QueryResultIterator<AppUser> iterator = query.iterator();
		while (iterator.hasNext()) {
			AppUser user = iterator.next();
			names.add(new StringResult(user.getUsername()));
		}

		return new CursoredSearchResult(iterator.getCursor().toWebSafeString(), new StringResultCollection(names));
	}

}

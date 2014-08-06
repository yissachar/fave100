package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.Song;
import com.fave100.server.domain.UserListResult;
import com.fave100.server.domain.UserListResultCollection;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.WhylineCollection;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.Constants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
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
			JsonReader reader = new JsonReader(new StringReader(content));
			reader.setLenient(true);
			final JsonElement jsonElement = parser.parse(reader);
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
	@Path(ApiPaths.GET_SONG_FAVELISTS)
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

	@GET
	@Path(ApiPaths.GET_SONG_WHYLINES)
	@ApiOperation(value = "Get whylines for a song", response = WhylineCollection.class)
	public static WhylineCollection getWhylines(@ApiParam(value = "ID of the song", required = true) @PathParam("id") final String id) {
		final List<Whyline> whylines = ofy().load().type(Whyline.class).filter("song", Ref.create(Key.create(Song.class, id))).limit(15).list();
		// Get the users avatars 
		// TODO: Should be a bulk query for efficiency
		for (final Whyline whyline : whylines) {
			final AppUser user = ofy().load().type(AppUser.class).id(whyline.getUsername().toLowerCase()).now();
			if (user != null)
				whyline.setAvatar(user.getAvatarImage());
		}
		return new WhylineCollection(whylines);
	}
}

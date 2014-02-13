package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.Song;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.WhylineCollection;
import com.fave100.server.domain.appuser.AppUser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("/" + ApiPaths.WHYLINE_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.WHYLINE_ROOT, description = "Operations on Whylines")
public class WhylineApi {

	@GET
	@Path(ApiPaths.GET_SONG_WHYLINES)
	@ApiOperation(value = "Get whylines for the song", response = WhylineCollection.class)
	public List<Whyline> getWhylinesForSong(@PathParam("id") final String id) {
		final List<Whyline> whylines = ofy().load().type(Whyline.class).filter("song", Ref.create(Key.create(Song.class, id))).limit(15).list();
		// Get the users avatars 
		// TODO: Should be a bulk query for efficiency
		for (final Whyline whyline : whylines) {
			final AppUser user = ofy().load().type(AppUser.class).id(whyline.getUsername().toLowerCase()).get();
			if (user != null)
				whyline.setAvatar(user.getAvatarImage());
		}
		return whylines;
	}
}

package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fave100.server.domain.appuser.AppUser;
import com.google.api.server.spi.config.ApiMethod;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

@Path("/" + ApiPaths.API_NAME + "/" + ApiPaths.API_VERSION + "/" + ApiPaths.WHYLINE_ROOT)
public class WhylineApi extends ApiBase {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ApiPaths.GET_SONG_WHYLINES)
	@ApiMethod(name = "whyline.getSongWhylines", path = ApiPaths.WHYLINE_ROOT + ApiPaths.GET_SONG_WHYLINES)
	public List<Whyline> getWhylinesForSong(@Named("id") @PathParam("id") final String id) {
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

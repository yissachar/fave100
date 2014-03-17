package com.fave100.server.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.StringResultCollection;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("/" + ApiPaths.TRENDING_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.TRENDING_ROOT, description = "Operations on Songs")
public class TrendingApi {

	@GET
	@Path(ApiPaths.TRENDING_FAVELISTS)
	@ApiOperation(value = "Get a list of trending FaveLists", response = StringResultCollection.class)
	public static StringResultCollection getTrendingFaveLists() {
		// Nov 26 2013: Temporarily disabling proper trending in favor of hard-coded popular lists
		//		List<Hashtag> hashtags = ofy().load().type(Hashtag.class).order("-zscore").limit(5).list();
		//		List<String> trending = new ArrayList<>();
		//		for (Hashtag hashtag : hashtags) {
		//			trending.add(hashtag.getName());
		//		}
		//		return trending;
		List<StringResult> trending = new ArrayList<>();
		trending.add(new StringResult("alltime"));
		trending.add(new StringResult("2014"));
		trending.add(new StringResult("2013"));
		trending.add(new StringResult("driving"));
		trending.add(new StringResult("running"));
		trending.add(new StringResult("wedding"));
		return new StringResultCollection(trending);
	}

}

package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.FeaturedLists;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.StringResultCollection;
import com.fave100.shared.Constants;
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

		// Return alltime, 2014 and up to 8 more randomly picked from the featured list
		List<StringResult> trending = new ArrayList<>();
		trending.add(new StringResult("alltime"));
		trending.add(new StringResult("2014"));

		FeaturedLists featuredLists = ofy().load().type(FeaturedLists.class).id(Constants.FEATURED_LISTS_ID).now();
		List<String> listNames = new ArrayList<>();
		if (featuredLists != null) {
			for (String list : featuredLists.getLists()) {
				listNames.add(list);
			}

			Collections.shuffle(listNames);
			while (trending.size() < 10 && listNames.size() > 0) {
				String randomList = listNames.get(0);
				listNames.remove(randomList);
				trending.add(new StringResult(randomList));
			}
		}

		return new StringResultCollection(trending);
	}

}

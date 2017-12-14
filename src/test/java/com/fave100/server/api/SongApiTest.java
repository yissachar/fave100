package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.fave100.server.TestHelper;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.WhylineCollection;
import com.fave100.server.domain.favelist.FaveItem;

public class SongApiTest extends ApiTest {

	@Before
	@Override
	public void setUp() {
		// Ensure queries will show up immediately
		setDefaultHighRepJobPolicyUnappliedJobPercentage((float)0.01);
		super.setUp();
	}

	@Test
	public void song_api_should_find_existing_song() {
		FaveItem faveItem = SongApi.getSong("BbK4Ex");
		assertThat(faveItem.getSong()).isEqualTo("Let Me Be Your Father X-Mas");
		assertThat(faveItem.getArtist()).isEqualTo("Boyzvoice");
	}

	@Test
	public void song_api_should_not_find_non_existing_song() {
		try {
			SongApi.getSong("jqjqjqjqjqjqjq");
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
		}
	}

	@Test
	public void song_api_should_find_existing_whylines() {
		String whylineText = "Because I like cows";
		String songID = "BbK4Ex";
		Whyline whyline = new Whyline(whylineText, songID, "arad", "meh");
		ofy().save().entity(whyline).now();

		WhylineCollection whylineCollection = SongApi.getWhylines(songID);
		assertThat(whylineCollection.getItems()).extracting("whyline").contains(whylineText);
	}

	@Test
	public void song_api_should_not_find_non_existing_whylines() {
		WhylineCollection whylineCollection = SongApi.getWhylines("BbK4Ex");
		assertThat(whylineCollection.getItems().size()).isEqualTo(0);
	}

	// TODO: Currently cannot test this api, since it expects a YouTube key to be loaded from the datastore (which does not exist in test container)
	//	@Test
	//	public void shouldGetYouTubeSearchResults() {
	//		YouTubeSearchResultCollection searchResults = webResource.path(ApiPaths.SONG_ROOT + ApiPaths.GET_YOUTUBE_SEARCH_RESULTS)
	//				.queryParam(ApiPaths.YOUTUBE_SEARCH_SONG_PARAM, "hello")
	//				.queryParam(ApiPaths.YOUTUBE_SEARCH_ARTIST_PARAM, "dragonette")
	//				.get(YouTubeSearchResultCollection.class);
	//
	//		assertNotEquals(searchResults.getItems(), null);
	//		assertNotEquals(searchResults.getItems().size(), 0);
	//	}
}

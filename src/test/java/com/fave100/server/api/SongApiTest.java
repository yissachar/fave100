package com.fave100.server.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.fave100.server.domain.favelist.FaveItem;

public class SongApiTest {

	@Test
	public void shouldGetExistingSong() {
		FaveItem faveItem = SongApi.getSong("BbK4Ex");//webResource.path(ApiPaths.SONG_ROOT + "/BbK4Ex").get(FaveItem.class);
		assertEquals(faveItem.getSong(), "Pangea");
		assertEquals(faveItem.getArtist(), "Professor Kliq");
	}

	@Test
	public void shouldNotGetNonExistingSong() {
		try {
			SongApi.getSong("jqjqjqjqjqjqjq");
			fail("Must throw exception");
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());
		}
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
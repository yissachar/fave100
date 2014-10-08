package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.fave100.server.domain.favelist.Hashtag;

public class SearchApiTest extends ApiTest {

	@Before
	@Override
	public void setUp() {
		// Ensure queries will show up immediately
		setDefaultHighRepJobPolicyUnappliedJobPercentage((float)0.01);
		super.setUp();
	}

	@Test
	public void search_api_should_find_lists_starting_with_single_letter_term() {
		Hashtag hashtag = new Hashtag("bah", "someuser");
		ofy().save().entity(hashtag).now();

		assertThat(SearchApi.searchFaveLists("b", null).getSearchResults().getItems().size()).isEqualTo(1);
	}

	@Test
	public void search_api_should_find_lists_starting_with_single_number_term() {
		Hashtag hashtag = new Hashtag("70sSongs", "bah");
		ofy().save().entity(hashtag).now();

		assertThat(SearchApi.searchFaveLists("7", null).getSearchResults().getItems().size()).isEqualTo(1);
	}

	@Test
	public void search_api_should_find_lists_starting_with_multi_term() {
		Hashtag hashtag = new Hashtag("awe7ome", "humbug");
		ofy().save().entity(hashtag).now();

		assertThat(SearchApi.searchFaveLists("awe7", null).getSearchResults().getItems().size()).isEqualTo(1);
	}

	@Test
	public void search_api_should_find_all_lists_starting_with_term() {
		Hashtag match1 = new Hashtag("theThing", "growl");
		Hashtag match2 = new Hashtag("tigerBrain", "munch");
		Hashtag match3 = new Hashtag("togreHandz", "chomp");
		ofy().save().entities(match1, match2, match3).now();

		assertThat(SearchApi.searchFaveLists("t", null).getSearchResults().getItems().size()).isEqualTo(3);
	}

	@Test
	public void search_api_should_ignore_all_lists_not_starting_with_term() {
		Hashtag match1 = new Hashtag("searchme", "aardvarks");
		Hashtag ignore1 = new Hashtag("dont", "enjoy");
		Hashtag ignore2 = new Hashtag("find", "delicate");
		Hashtag ignore3 = new Hashtag("me", "strawberries");
		ofy().save().entities(match1, ignore1, ignore2, ignore3).now();

		assertThat(SearchApi.searchFaveLists("s", null).getSearchResults().getItems().size()).isEqualTo(1);
	}

}
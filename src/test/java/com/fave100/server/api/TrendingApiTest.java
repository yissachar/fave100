package com.fave100.server.api;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TrendingApiTest extends ApiTest {

	@Test
	public void trending_api_should_get_trending_lists() {
		assertTrue(!TrendingApi.getTrendingFaveLists().getItems().isEmpty());
	}
}

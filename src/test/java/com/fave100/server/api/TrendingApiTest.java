package com.fave100.server.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TrendingApiTest extends ApiTest {

	@Test
	public void trending_api_should_get_trending_lists() {
		assertThat(TrendingApi.getTrendingFaveLists().getItems()).isNotEmpty();
	}

	@Test
	public void trending_api_should_always_contain_alltime_and_2014_lists() {
		assertThat(TrendingApi.getTrendingFaveLists().getItems()).extracting("value")
				.contains("alltime", "2014");
	}
}

package com.fave100.shared.requestfactory;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

public interface ApplicationRequestFactory extends RequestFactory {

	AppUserRequest appUserRequest();

	SongRequest songRequest();

	FaveListRequest faveListRequest();

	WhylineRequest whylineRequest();

	ExploreResultRequest exploreRequest();
}

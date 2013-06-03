package com.fave100.shared.requestfactory;

import com.fave100.server.domain.YouTubeSearchResult;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(YouTubeSearchResult.class)
public interface YouTubeSearchResultProxy extends ValueProxy {
	String getVideoId();

	String getThumbnail();
}
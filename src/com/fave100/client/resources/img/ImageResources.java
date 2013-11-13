package com.fave100.client.resources.img;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ImageResources extends ClientBundle {

	@Source("logo.png")
	ImageResource logo();

	@Source("logo-large.png")
	ImageResource logoLarge();

	@Source("loading-indicator.gif")
	ImageResource loadingIndicator();

	@Source("add.png")
	ImageResource add();

	@Source("delete.png")
	ImageResource delete();

	@Source("down-arrow.png")
	ImageResource downArrow();

	@Source("up-arrow.png")
	ImageResource upArrow();

	@Source("help-bubble-arrow.png")
	ImageResource helpBubbleArrow();

	@Source("search-loading-indicator.gif")
	ImageResource searchLoadingIndicator();

	@Source("triangle.png")
	ImageResource triangle();

	@Source("g-plus-icon.png")
	ImageResource gPlusIcon();

	@Source("twitter_logo_white.png")
	ImageResource twitterLogo();

	@Source("fb_logo.png")
	ImageResource fbLogo();

}

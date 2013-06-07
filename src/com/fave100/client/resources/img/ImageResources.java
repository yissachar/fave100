package com.fave100.client.resources.img;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ImageResources extends ClientBundle {

	@Source("logo.png")
	ImageResource logo();

	@Source("logo-large.png")
	ImageResource logoLarge();

	@Source("home-page-tablet.png")
	ImageResource homePageTablet();

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

}
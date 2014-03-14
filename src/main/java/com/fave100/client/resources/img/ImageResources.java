package com.fave100.client.resources.img;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ImageResources extends ClientBundle {

	public static ImageResources INSTANCE = GWT.create(ImageResources.class);

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

	@Source("/about/about_screen_1.png")
	ImageResource aboutScreen1();

	@Source("/about/about_screen_2.png")
	ImageResource aboutScreen2();

	@Source("/about/about_screen_3.png")
	ImageResource aboutScreen3();

	@Source("/about/about_screen_4.png")
	ImageResource aboutScreen4();

	@Source("/about/about_screen_5.png")
	ImageResource aboutScreen5();

	@Source("left-arrow.png")
	ImageResource leftArrow();

	@Source("right-arrow.png")
	ImageResource rightArrow();

	@Source("corey.jpg")
	ImageResource corey();

}

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

	@Source("search-loading-indicator-white.gif")
	ImageResource searchLoadingIndicatorWhite();

	@Source("search-loading-indicator-black.gif")
	ImageResource searchLoadingIndicatorBlack();

	@Source("triangle.png")
	ImageResource triangle();

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

	@Source("corey.jpg")
	ImageResource corey();

	@Source("album.png")
	ImageResource album();

}

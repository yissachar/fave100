package com.fave100.client;

import com.google.gwt.user.client.ui.Image;

public class LoadingIndicator {

	private static Image _image;
	// Number of active processes indicating loading
	private static int showCount;

	public static void init(final Image image) {
		_image = image;
		hide();
	}

	public static void show() {
		showCount++;
		_image.setVisible(true);
	}

	public static void hide() {
		showCount--;

		if (showCount < 0)
			showCount = 0;

		if (showCount == 0)
			_image.setVisible(false);
	}

}

package com.fave100.client.widgets;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;

public class ImageHyperlink extends Hyperlink {

	public ImageHyperlink() {
	}

	public void setResource(final ImageResource imageResource) {
		final Image img = new Image(imageResource);
		DOM.insertBefore(getElement(), img.getElement(), DOM.getFirstChild(getElement()));
	}
}

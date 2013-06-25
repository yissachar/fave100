package com.fave100.client.resources.mobile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface MobileResources extends ClientBundle {
	public static final MobileResources INSTANCE = GWT.create(MobileResources.class);

	@Source("mobile-override.css")
	public CssResource css();
}

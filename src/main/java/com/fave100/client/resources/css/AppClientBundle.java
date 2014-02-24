package com.fave100.client.resources.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface AppClientBundle extends ClientBundle {

	public static final AppClientBundle INSTANCE = GWT.create(AppClientBundle.class);

	@Source("global.css")
	public CssResource getGlobalCss();

	@Source("mobile-override.css")
	public CssResource getMobileCss();
}

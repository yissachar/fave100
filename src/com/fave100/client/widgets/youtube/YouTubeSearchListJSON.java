package com.fave100.client.widgets.youtube;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * JavaScript Overlay
 * 
 */
public class YouTubeSearchListJSON extends JavaScriptObject {

	protected YouTubeSearchListJSON() {
	}

	public final native JsArray<YouTubeJSONItem> getItems() /*-{
															return this.items;
															}-*/;

	public static final native JavaScriptObject searchListFromJson(String json) /*-{
																				return eval(json);
																				}-*/;

}

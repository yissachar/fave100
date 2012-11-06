package com.fave100.client.pages.search;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * JavaSrcipt Overlay
 *
 */
public class JSONResult extends JavaScriptObject {
	
	protected JSONResult() {}

	public final native String getTrackName() /*-{
		return this[0];
	}-*/;

	public final native String getArtistName() /*-{
		return this[1];
	}-*/;

	public final native String getMbid() /*-{
		return this[2];
	}-*/;
}

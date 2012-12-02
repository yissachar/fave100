package com.fave100.client.pages.song;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * JavaScript Overlay
 *
 */
public class YouTubeJSONItem extends JavaScriptObject{

	protected YouTubeJSONItem() {}

	public final native String getVideoId() /*-{
		return this.id.videoId;
	}-*/;

	public final native String getThumbnail() /*-{
		return this.snippet.thumbnails['default'].url;
	}-*/;

}

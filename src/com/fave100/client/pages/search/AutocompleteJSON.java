package com.fave100.client.pages.search;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * JavaScript Overlay
 *
 */
public class AutocompleteJSON extends JavaScriptObject {
	
	protected AutocompleteJSON() {}

	public final native JsArray<JSONResult> getEntries() /*-{
	   	return this.results;
	}-*/;
}

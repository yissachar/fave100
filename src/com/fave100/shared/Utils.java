package com.fave100.shared;

public class Utils {

	public static native boolean isTouchDevice() /*-{
		return !!('ontouchstart' in $wnd) || !!('onmsgesturechange' in $wnd);
	}-*/;
}

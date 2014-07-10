package com.fave100.client;

import java.util.Iterator;

import com.fave100.shared.Constants;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class Utils {

	public static native boolean isTouchDevice() /*-{
		return !!('ontouchstart' in $wnd) || !!('onmsgesturechange' in $wnd);
	}-*/;

	public static boolean isSmallDisplay() {
		return RootPanel.get().getStyleName().contains(Fave100Bootstrapper.SMALL_DISPLAY_STYLE);
	}

	public static boolean isMediumDisplay() {
		return RootPanel.get().getStyleName().contains(Fave100Bootstrapper.MEDIUM_DISPLAY_STYLE);
	}

	public static boolean isLargeDisplay() {
		return RootPanel.get().getStyleName().contains(Fave100Bootstrapper.LARGE_DISPLAY_STYLE);
	}

	public static boolean widgetContainsElement(final Widget widget, final Element element) {
		if (widget instanceof HasWidgets) {
			final Iterator<Widget> iter = ((HasWidgets)widget).iterator();
			while (iter.hasNext()) {
				final Widget child = iter.next();
				if ((child != widget && widgetContainsElement(child, element)) || element.equals(child.getElement()) || element.equals(widget.getElement()))
					return true;
			}
		}
		return element.equals(widget.getElement());
	}

	public static String rankToColor(int rank, int numItems) {
		if (rank <= 0 || rank > Constants.MAX_ITEMS_PER_LIST)
			throw new IllegalArgumentException("Rank cannot be less than 1 or greater than " + Constants.MAX_ITEMS_PER_LIST);

		double increment = 210.0 / numItems;
		int hue = (int)(rank * increment);
		return "hsl(" + hue + ", 70%, 60%)";
	}
}

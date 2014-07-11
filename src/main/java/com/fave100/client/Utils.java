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

		int rightRed = 82;
		int rightGreen = 153;
		int rightBlue = 224;

		int leftRed = 224;
		int leftGreen = 86;
		int leftBlue = 82;

		double p = (double)rank / numItems;
		int red2 = (int)((1.0 - p) * leftRed + p * rightRed + 0.5);
		int green2 = (int)((1.0 - p) * leftGreen + p * rightGreen + 0.5);
		int blue2 = (int)((1.0 - p) * leftBlue + p * rightBlue + 0.5);

		return "rgb(" + red2 + "," + green2 + "," + blue2 + ")";
	}
}

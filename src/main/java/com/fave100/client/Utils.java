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

		int[][] gradients = { {224, 86, 82}, {214, 188, 40}, {82, 153, 224}};

		double itemsPerGradientStep = ((double)numItems / (gradients.length - 1));
		int gradientPicker = (int)Math.floor(((rank - 1) / itemsPerGradientStep));
		int[] leftGradient = gradients[gradientPicker];
		int[] rightGradient = gradients[gradientPicker + 1];

		double percent = (double)((rank - 1) % itemsPerGradientStep) / itemsPerGradientStep;
		int red = (int)((1.0 - percent) * leftGradient[0] + percent * rightGradient[0]);
		int green = (int)((1.0 - percent) * leftGradient[1] + percent * rightGradient[1]);
		int blue = (int)((1.0 - percent) * leftGradient[2] + percent * rightGradient[2]);

		return "rgb(" + red + "," + green + "," + blue + ")";
	}
}

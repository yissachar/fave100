package com.fave100.client;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.user.client.ui.Label;

/**
 * A notification will alert the user to various events and errors.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class Notification {

	public Notification() {
	}

	public static Label label;

	public static void init(final Label _label) {
		label = _label;
	}

	public static void show(final String message) {
		label.setText(message);
		Notification.show(message, false, 3000);
	}

	public static void show(final String message, final boolean error) {
		final int delayTime = error ? 5000 : 3000;
		Notification.show(message, error, delayTime);
	}

	public static void show(final String message, final boolean error,
			final int delay) {
		label.setVisible(true);
		if (error) {
			label.addStyleName("error");
		}
		else {
			label.removeStyleName("error");
		}
		$(label).delay(delay).fadeOut(300);
	}
}

package com.fave100.client.widgets;

import static com.google.gwt.query.client.GQuery.$;

import com.fave100.client.resources.css.GlobalStyle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FadeText extends Composite {

	public static final int SLOW = 4500;
	public static final int NORMAL = 2500;
	public static final int FAST = 1500;

	private static FadeTextUiBinder uiBinder = GWT.create(FadeTextUiBinder.class);

	interface FadeTextUiBinder extends UiBinder<Widget, FadeText> {
	}

	interface FadeTextStyle extends GlobalStyle {

	}

	@UiField FadeTextStyle style;
	@UiField Label label;

	public FadeText() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setText(String text) {
		setText(text, false);
	}

	public void setText(String text, boolean error) {
		setText(text, error, FadeText.NORMAL);
	}

	public void setText(String text, boolean error, int milliseconds) {
		if (error) {
			label.addStyleName(style.error());
		}
		else {
			label.removeStyleName(style.error());
		}

		label.setText(text);
		label.setVisible(true);
		$(label).delay(milliseconds).fadeOut(500);
	}
}

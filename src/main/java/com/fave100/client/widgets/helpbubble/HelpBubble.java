package com.fave100.client.widgets.helpbubble;

import com.fave100.client.widgets.Icon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A pop-up widget that displays a simple text message and disappears when the user dismisses it.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class HelpBubble extends Composite {

	private static HelpBubbleUiBinder uiBinder = GWT.create(HelpBubbleUiBinder.class);

	interface HelpBubbleUiBinder extends UiBinder<Widget, HelpBubble> {
	}

	@UiField Label helpText;
	@UiField Icon closeButton;
	@UiField Panel arrow;

	public HelpBubble(final String title, final String text, final int width) {
		initWidget(uiBinder.createAndBindUi(this));
		helpText.setText(text);
		setWidth(width + "px");
	}

	@UiHandler("closeButton")
	void onClick(final ClickEvent event) {
		setVisible(false);
	}

	public void setArrowPos(final double percent) {
		if (percent < 0.0 || percent > 100.0)
			return;

		arrow.getElement().getStyle().setLeft(percent, Unit.PCT);
	}

}

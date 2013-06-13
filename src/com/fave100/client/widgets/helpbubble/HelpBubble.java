package com.fave100.client.widgets.helpbubble;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HelpBubble extends Composite {

	private static HelpBubbleUiBinder uiBinder = GWT.create(HelpBubbleUiBinder.class);

	interface HelpBubbleUiBinder extends UiBinder<Widget, HelpBubble> {
	}

	@UiField Label helpTitle;
	@UiField Label helpText;
	@UiField Label closeButton;
	@UiField Image arrow;

	public static enum Direction {
		LEFT,
		RIGHT,
		UP,
		DOWN
	}

	public HelpBubble(final String title, final String text, final int width, final Direction direction) {
		initWidget(uiBinder.createAndBindUi(this));
		helpTitle.setText(title);
		helpText.setText(text);
		setWidth(width + "px");
		switch (direction) {
			case LEFT:
				//$(arrow).css("transform:", "rotate(270deg)");
				break;

			default:
				break;
		}
	}

	@UiHandler("closeButton")
	void onClick(final ClickEvent event) {
		setVisible(false);
	}

	public void setArrowPos(final int x) {
		final GQuery $arrow = $(arrow);
		$arrow.css("position", "absolute");
		$arrow.css("left", String.valueOf(x) + "px");
	}

}

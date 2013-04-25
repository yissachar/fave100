package com.fave100.client.pages.users.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ShareButton extends Composite {

	private static ShareButtonUiBinder uiBinder = GWT.create(ShareButtonUiBinder.class);

	interface ShareButtonUiBinder extends UiBinder<Widget, ShareButton> {
	}

	boolean _ownList;

	@UiField FocusPanel shareArea;
	@UiField HTMLPanel socialDropdown;
	@UiField Image twitterShare;
	@UiField DivElement fbLike;
	private String twitterMessage;

	public ShareButton() {
		initWidget(uiBinder.createAndBindUi(this));
		// Set Facebook like URL
		fbLike.setAttribute("data-href", Window.Location.getHref());

	}

	@UiHandler("twitterShare")
	void onTwitterClick(final ClickEvent event) {
		final String url = "http://twitter.com/share?text=" + twitterMessage + Window.Location.getHref();
		Window.open(url, "_blank", "width=600, height=300");
	}

	public void setOwnList(final boolean ownList) {
		_ownList = ownList;
	}

	public void setTwitterMessage(final String message) {
		twitterMessage = message;
	}

}

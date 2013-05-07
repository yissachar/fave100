package com.fave100.client.pages.users.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ShareButton extends Composite {

	private static ShareButtonUiBinder uiBinder = GWT.create(ShareButtonUiBinder.class);

	interface ShareButtonUiBinder extends UiBinder<Widget, ShareButton> {
	}

	boolean _ownList;

	@UiField FocusPanel shareArea;
	@UiField HTMLPanel shareContainer;
	@UiField HTMLPanel gplusPlaceholder;
	@UiField TextBox shareLink;
	@UiField AnchorElement twitterShare;
	@UiField DivElement fbLike;

	public ShareButton() {
		initWidget(uiBinder.createAndBindUi(this));
		// Set Facebook like URL
		fbLike.setAttribute("data-href", Window.Location.getHref());
		// Set share link
		shareLink.setText(Window.Location.getHref());
		final String s = "<g:plusone href='" + Window.Location.getHref() + "'></g:plusone>";
		final HTML h = new HTML(s);
		gplusPlaceholder.add(h);
	}

	@UiHandler("shareLink")
	void onClick(final ClickEvent event) {
		shareLink.setSelectionRange(0, shareLink.getText().length());
	}

	public void setOwnList(final boolean ownList) {
		_ownList = ownList;
	}

	public void setTwitterMessage(final String message) {
		twitterShare.setAttribute("data-text", message + Window.Location.getHref());
	}

}

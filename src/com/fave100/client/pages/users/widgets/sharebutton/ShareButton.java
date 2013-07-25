package com.fave100.client.pages.users.widgets.sharebutton;

import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ShareButton extends Composite {

	private static ShareButtonUiBinder uiBinder = GWT.create(ShareButtonUiBinder.class);

	interface ShareButtonUiBinder extends UiBinder<Widget, ShareButton> {
	}

	boolean _ownList;

	@UiField HTMLPanel shareContainer;
	@UiField HTMLPanel gplusPlaceholder;
	@UiField HTMLPanel twitterContainer;
	@UiField TextBox shareLink;
	@UiField DivElement fbLike;
	HTML gplus;
	private String twitterMessage;

	public ShareButton() {
		initWidget(uiBinder.createAndBindUi(this));
		final String s = "<g:plusone href='" + Window.Location.getHref() + "'></g:plusone>";
		gplus = new HTML(s);
		gplusPlaceholder.add(gplus);
	}

	@UiHandler("shareLink")
	void onClick(final ClickEvent event) {
		shareLink.setSelectionRange(0, shareLink.getText().length());
	}

	public void setSharingUrls(final String username) {
		final String shareUrl = new UrlBuilder(NameTokens.users).with(UsersPresenter.USER_PARAM, username).getUrl();
		// Set Facebook like URL
		fbLike.setAttribute("data-href", shareUrl);

		// Set Google+ URL
		gplus.getElement().setAttribute("href", shareUrl);

		// Clear Twitter button
		for (int i = 0; i < twitterContainer.getElement().getChildCount(); i++) {
			twitterContainer.getElement().getChild(i).removeFromParent();
		}
		// Rebuild Twitter button
		final Anchor twitterShare = new Anchor();
		twitterShare.setText("Tweet");
		twitterShare.setHref("https://twitter.com/share");
		twitterShare.addStyleName("twitter-share-button");
		twitterShare.getElement().setAttribute("data-lang", "en");
		twitterShare.getElement().setAttribute("data-size", "medium");
		twitterShare.getElement().setAttribute("data-count", "horizontal");
		twitterShare.getElement().setAttribute("data-text", twitterMessage);
		twitterShare.getElement().setAttribute("data-url", shareUrl);
		twitterContainer.add(twitterShare);

		// Set share link
		shareLink.setText(shareUrl);

	}

	public void setOwnList(final boolean ownList) {
		_ownList = ownList;
	}

	public void setTwitterMessage(final String message) {
		twitterMessage = message;
		//	twitterShare.setAttribute("data-text", message + Window.Location.getHref());
	}

}

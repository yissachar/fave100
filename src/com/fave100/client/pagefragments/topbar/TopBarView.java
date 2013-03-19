package com.fave100.client.pagefragments.topbar;

import com.fave100.client.Notification;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class TopBarView extends ViewWithUiHandlers<TopBarUiHandlers> implements
		TopBarPresenter.MyView {

	private final Widget	widget;

	public interface Binder extends UiBinder<Widget, TopBarView> {
	}

	@UiField
	InlineHyperlink	logInLogOutLink;
	@UiField
	Label			notification;
	@UiField
	SpanElement		greeting;
	@UiField
	Anchor			myFave100Link;
	@UiField
	InlineHyperlink	registerLink;
	@UiField
	HTMLPanel		loginBox;

	@Inject
	public TopBarView(final Binder binder,
			final ApplicationRequestFactory requestFactory) {

		widget = binder.createAndBindUi(this);
		Notification.init(notification);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final Widget content) {
		super.setInSlot(slot, content);

		if (slot == TopBarPresenter.LOGIN_SLOT) {
			loginBox.clear();
			if (content != null) {
				loginBox.add(content);
			}
		}
	}

	@Override
	public void setLoggedIn(final String username) {
		greeting.setInnerHTML(username);
		myFave100Link.setVisible(true);
		myFave100Link.setHref(Window.Location.getPath()
				+ Window.Location.getQueryString() + "#"
				+ NameTokens.getUsers() + ";" + UsersPresenter.USER_PARAM + "="
				+ username);
		registerLink.setVisible(false);
		logInLogOutLink.setText("Log out");
		logInLogOutLink.setTargetHistoryToken(NameTokens.logout);
		loginBox.setVisible(false);
	}

	@Override
	public void setLoggedOut() {
		greeting.setInnerHTML("");
		myFave100Link.setVisible(false);
		registerLink.setVisible(true);
		logInLogOutLink.setText("Log in");
		logInLogOutLink.setTargetHistoryToken(NameTokens.login);
		loginBox.setVisible(true);
	}
}

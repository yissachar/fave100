package com.fave100.client.pagefragments.topbar;

import com.fave100.client.LoadingIndicator;
import com.fave100.client.Notification;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.widgets.ImageHyperlink;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class TopBarView extends ViewWithUiHandlers<TopBarUiHandlers> implements
		TopBarPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, TopBarView> {
	}

	public interface TopBarStyle extends CssResource {
		String topBarDropShadow();
	}

	@UiField TopBarStyle style;
	@UiField HTMLPanel topBar;
	@UiField Image loadingIndicator;
	@UiField ImageHyperlink logoLink;
	@UiField FlowPanel userInfo;
	@UiField Image userAvatar;
	@UiField InlineLabel usernameLabel;
	@UiField Hyperlink listLink;
	@UiField Hyperlink logOutLink;
	@UiField InlineLabel loginButton;
	@UiField Label notification;

	//	@UiField InlineLabel registerButton;

	@Inject
	public TopBarView(final Binder binder,
						final ApplicationRequestFactory requestFactory) {

		widget = binder.createAndBindUi(this);
		Notification.init(notification);
		LoadingIndicator.init(loadingIndicator);
		userInfo.setVisible(false);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("loginButton")
	void onLoginClick(final ClickEvent event) {
		getUiHandlers().showLoginBox();
	}

	/*
		@UiHandler("registerButton")
		void onRegisterClick(final ClickEvent event) {
			getUiHandlers().showRegisterBox();
		}*/

	@Override
	public void setLoggedIn(final String username, final String avatar) {
		//		registerButton.setVisible(false);
		loginButton.setVisible(false);
		logOutLink.setVisible(true);
		logOutLink.setText("Sign out");
		logOutLink.setTargetHistoryToken(NameTokens.logout);
		final String userPlace = new UrlBuilder(NameTokens.lists).with(ListPresenter.USER_PARAM, username).getPlaceToken();
		listLink.setTargetHistoryToken(userPlace);
		logoLink.setTargetHistoryToken(userPlace);
		usernameLabel.setText(username);
		userAvatar.setUrl(avatar);
		userInfo.setVisible(true);
	}

	@Override
	public void setLoggedOut() {
		//		registerButton.setVisible(true);
		loginButton.setVisible(true);
		logOutLink.setVisible(false);
		logoLink.setTargetHistoryToken(NameTokens.home);
		userInfo.setVisible(false);
	}

	@Override
	public void setTopBarDropShadow(final boolean show) {
		if (show) {
			topBar.addStyleName(style.topBarDropShadow());
		}
		else {
			topBar.removeStyleName(style.topBarDropShadow());
		}
	}
}

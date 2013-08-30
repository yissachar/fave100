package com.fave100.client.pagefragments.topbar;

import com.fave100.client.LoadingIndicator;
import com.fave100.client.Notification;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.widgets.ImageHyperlink;
import com.fave100.shared.Constants;
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
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

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
	@UiField FlowPanel loggedInContainer;
	@UiField InlineLabel usernameLabel;
	@UiField Hyperlink listLink;
	@UiField Hyperlink logOutLink;
	@UiField InlineLabel loginButton;
	@UiField Label notification;

	@Inject
	public TopBarView(final Binder binder, final ApplicationRequestFactory requestFactory) {

		widget = binder.createAndBindUi(this);
		Notification.init(notification);
		LoadingIndicator.init(loadingIndicator);
		loggedInContainer.setVisible(false);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("loginButton")
	void onLoginClick(final ClickEvent event) {
		getUiHandlers().showLoginBox();
	}

	@Override
	public void setLoggedIn(final String username) {
		loggedInContainer.setVisible(true);
		loginButton.setVisible(false);
		logOutLink.setVisible(true);
		logOutLink.setText("Sign out");
		logOutLink.setTargetHistoryToken(NameTokens.logout);
		final String userPlace = new ParameterTokenFormatter()
				.toPlaceToken(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(ListPresenter.USER_PARAM, username)
						.build());
		listLink.setTargetHistoryToken(userPlace);
		final String listPlace = new ParameterTokenFormatter()
				.toPlaceToken(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(ListPresenter.LIST_PARAM, Constants.DEFAULT_HASHTAG)
						.build());
		logoLink.setTargetHistoryToken(listPlace);
		usernameLabel.setText(username);
	}

	@Override
	public void setLoggedOut() {
		loggedInContainer.setVisible(false);
		loginButton.setVisible(true);
		logOutLink.setVisible(false);
		logoLink.setTargetHistoryToken(NameTokens.home);
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

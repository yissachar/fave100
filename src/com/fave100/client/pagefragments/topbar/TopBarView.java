package com.fave100.client.pagefragments.topbar;

import com.fave100.client.LoadingIndicator;
import com.fave100.client.Notification;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
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
	@UiField InlineHyperlink logOutLink;
	@UiField InlineLabel loginButton;
	@UiField Label notification;
	@UiField Anchor greeting;
	@UiField InlineHyperlink registerLink;
	@UiField HTMLPanel loginBox;
	@UiField HTMLPanel loginLightBox;
	@UiField FocusPanel lightBoxBackground;

	@Inject
	public TopBarView(final Binder binder,
						final ApplicationRequestFactory requestFactory) {

		widget = binder.createAndBindUi(this);
		loginLightBox.setVisible(false);
		Notification.init(notification);
		LoadingIndicator.init(loadingIndicator);
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

	@UiHandler("lightBoxBackground")
	void onLightBoxClick(final ClickEvent event) {
		hideLightbox();
	}

	@UiHandler("loginButton")
	void onlogInClick(final ClickEvent event) {
		loginLightBox.setVisible(true);
	}

	@Override
	public void hideLightbox() {
		loginLightBox.setVisible(false);
		getUiHandlers().clearLoginBox();
	}

	@Override
	public void setLoggedIn(final String username) {
		greeting.setText(username);
		greeting.setVisible(true);
		final String userPlace = new UrlBuilder(NameTokens.users)
				.with(UsersPresenter.USER_PARAM, username)
				.getUrl();
		greeting.setHref(userPlace);
		registerLink.setVisible(false);
		loginButton.setVisible(false);
		logOutLink.setVisible(true);
		logOutLink.setTargetHistoryToken(NameTokens.logout);
		loginBox.setVisible(false);
		hideLightbox();
	}

	@Override
	public void setLoggedOut() {
		greeting.setText("");
		greeting.setVisible(false);
		registerLink.setVisible(true);
		loginButton.setVisible(true);
		logOutLink.setVisible(false);
		logOutLink.setText("Log in");
		loginBox.setVisible(true);
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

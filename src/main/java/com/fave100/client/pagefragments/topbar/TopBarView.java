package com.fave100.client.pagefragments.topbar;

import com.fave100.client.Notification;
import com.fave100.client.Utils;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.client.widgets.Icon;
import com.fave100.shared.Constants;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class TopBarView extends ViewWithUiHandlers<TopBarUiHandlers> implements
		TopBarPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, TopBarView> {
	}

	public interface TopBarStyle extends GlobalStyle {
		String topBarDropShadow();

		String floatingSearch();
	}

	@UiField TopBarStyle style;
	@UiField Panel slideOutBackground;
	@UiField HTMLPanel topBar;
	@UiField Icon menuBar;
	@UiField Hyperlink logoFaveText;
	@UiField Hyperlink logo100Text;
	@UiField FlowPanel loggedInContainer;
	@UiField InlineLabel usernameLabel;
	@UiField Hyperlink listLink;
	@UiField InlineLabel logOutButton;
	@UiField InlineLabel loginButton;
	@UiField Label notification;
	@UiField SimplePanel unifiedSearchContainer;
	@UiField HTMLPanel unifiedSearch;
	@UiField Icon searchToggle;
	private ParameterTokenFormatter _parameterTokenFormatter;

	@Inject
	public TopBarView(final Binder binder, ParameterTokenFormatter parameterTokenFormatter) {
		widget = binder.createAndBindUi(this);
		_parameterTokenFormatter = parameterTokenFormatter;
		Notification.init(notification);
		loggedInContainer.setVisible(false);

		RootPanel.get().addHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Element target = Element.as(event.getNativeEvent().getEventTarget());
				if (Utils.widgetContainsElement(slideOutBackground, target)) {
					setFloatingSearch(false);
				}
			}
		}, ClickEvent.getType());
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (content == null)
			return;

		if (slot == TopBarPresenter.SEARCH_SLOT) {
			unifiedSearch.clear();
			unifiedSearch.add(content);
		}
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("menuBar")
	void onMenuBarClick(final ClickEvent event) {
		getUiHandlers().fireHideSideBarEvent();
	}

	@UiHandler("searchToggle")
	void onSearchToggleclick(final ClickEvent event) {
		if (unifiedSearchContainer.getStyleName().contains(style.floatingSearch())) {
			setFloatingSearch(false);
		}
		else {
			setFloatingSearch(true);
		}
	}

	@UiHandler("loginButton")
	void onLoginClick(final ClickEvent event) {
		getUiHandlers().showLoginDialog();
	}

	@UiHandler("logOutButton")
	void onLogoutClick(final ClickEvent event) {
		getUiHandlers().logout();
	}

	@Override
	public void setLoggedIn(final String username) {
		loggedInContainer.setVisible(true);
		loginButton.setVisible(false);
		logOutButton.setVisible(true);
		final String userPlace = _parameterTokenFormatter
				.toPlaceToken(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.USER_PARAM, username)
						.build());
		listLink.setTargetHistoryToken(userPlace);
		final String listPlace = _parameterTokenFormatter
				.toPlaceToken(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.LIST_PARAM, Constants.DEFAULT_HASHTAG)
						.build());
		logoFaveText.setTargetHistoryToken(listPlace);
		logo100Text.setTargetHistoryToken(listPlace);
		usernameLabel.setText(username);
	}

	@Override
	public void setLoggedOut() {
		loggedInContainer.setVisible(false);
		loginButton.setVisible(true);
		logOutButton.setVisible(false);
		final String listPlace = _parameterTokenFormatter
				.toPlaceToken(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.LIST_PARAM, Constants.DEFAULT_HASHTAG)
						.build());
		logoFaveText.setTargetHistoryToken(listPlace);
		logo100Text.setTargetHistoryToken(listPlace);
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

	@Override
	public void setMobileView(String currentPlace) {
		menuBar.setVisible((Utils.isMediumDisplay() || Utils.isSmallDisplay()) && currentPlace.equals(NameTokens.lists));
		searchToggle.setVisible(Utils.isSmallDisplay());
		if (Utils.isSmallDisplay()) {
			unifiedSearchContainer.setHeight((Window.getClientHeight() - Constants.TOP_BAR_HEIGHT) + "px");
		}
		else {
			unifiedSearchContainer.setHeight("auto");
		}
	}

	@Override
	public void setFloatingSearch(boolean floating) {
		if (floating) {
			unifiedSearchContainer.addStyleName(style.floatingSearch());
			slideOutBackground.addStyleName(style.floatingSearch());
		}
		else {
			unifiedSearchContainer.removeStyleName(style.floatingSearch());
			slideOutBackground.removeStyleName(style.floatingSearch());
		}
	}
}

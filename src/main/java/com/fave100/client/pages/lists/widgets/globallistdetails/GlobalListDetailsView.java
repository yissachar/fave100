package com.fave100.client.pages.lists.widgets.globallistdetails;

import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class GlobalListDetailsView extends ViewWithUiHandlers<GlobalListDetailsUiHandlers> implements GlobalListDetailsPresenter.MyView {

	private final static int LIST_NAME_FONT_SIZE = 28;

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, GlobalListDetailsView> {
	}

	interface Style extends GlobalStyle {
	}

	@UiField Style style;
	@UiField FlowPanel container;
	@UiField FlowPanel trendingLists;
	@UiField Panel tagline;

	private ParameterTokenFormatter _parameterTokenFormatter;

	@Inject
	public GlobalListDetailsView(final Binder binder, ParameterTokenFormatter parameterTokenFormatter) {
		widget = binder.createAndBindUi(this);
		_parameterTokenFormatter = parameterTokenFormatter;
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("registerLink")
	void onRegisterLinkClick(final ClickEvent event) {
		getUiHandlers().showRegister();
	}

	@Override
	public void setTrendingLists(final String hashtag, final List<String> lists, CurrentUser currentUser) {

		trendingLists.clear();
		for (String list : lists) {
			InlineHyperlink link = new InlineHyperlink();
			link.setTargetHistoryToken(_parameterTokenFormatter
					.toPlaceToken(new PlaceRequest.Builder()
							.nameToken(NameTokens.lists)
							.with(PlaceParams.LIST_PARAM, list)
							.build()));
			link.setText(list);
			trendingLists.add(link);
		}

		// Only show call action to users who are not logged in
		tagline.setVisible(!currentUser.isLoggedIn());
	}

	@Override
	public void show() {
		widget.setVisible(true);
	}

	@Override
	public void hide() {
		widget.setVisible(false);
	}
}

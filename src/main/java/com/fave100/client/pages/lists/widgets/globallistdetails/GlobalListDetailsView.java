package com.fave100.client.pages.lists.widgets.globallistdetails;

import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.shared.Constants;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class GlobalListDetailsView extends ViewWithUiHandlers<GlobalListDetailsUiHandlers> implements GlobalListDetailsPresenter.MyView {

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
		resize();

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				resize();
			}
		});
		resize();
	}

	private void resize() {
		container.getElement().getStyle().setProperty("minHeight", ((Window.getClientHeight() - Constants.TOP_BAR_HEIGHT - 10) + "px"));
	}

	@Override
	public Widget asWidget() {
		return widget;
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

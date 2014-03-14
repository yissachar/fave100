package com.fave100.client.pages.lists.widgets.globallistdetails;

import java.util.List;

import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.shared.Constants;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class GlobalListDetailsView extends ViewWithUiHandlers<GlobalListDetailsUiHandlers> implements GlobalListDetailsPresenter.MyView {

	private final static int LIST_NAME_FONT_SIZE = 56;

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, GlobalListDetailsView> {
	}

	interface Style extends GlobalStyle {
		String mobile();
	}

	@UiField Style style;
	@UiField FlowPanel container;
	@UiField Label hashtagLabel;
	@UiField Anchor contributeCTA;
	@UiField HTMLPanel listAutocomplete;
	@UiField FlowPanel trendingLists;

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

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {

		if (slot == GlobalListDetailsPresenter.LIST_AUTOCOMPLETE_SLOT) {
			listAutocomplete.clear();
			if (content != null) {
				listAutocomplete.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@UiHandler("contributeCTA")
	void onContributeClick(ClickEvent event) {
		getUiHandlers().contributeToList();
	}

	@Override
	public void setInfo(final String hashtag) {
		hashtagLabel.setText(hashtag);
		// Shrink the list name until it fits into the sidebar
		int fontSize = LIST_NAME_FONT_SIZE;
		do {
			hashtagLabel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
			fontSize--;
		} while (hashtagLabel.getElement().getClientWidth() > style.SIDE_BAR_WIDTH());

	}

	@Override
	public void setTrendingLists(final List<String> lists) {
		trendingLists.clear();
		for (String list : lists) {
			InlineHyperlink link = new InlineHyperlink();
			link.setTargetHistoryToken(_parameterTokenFormatter
					.toPlaceToken(new PlaceRequest.Builder()
							.nameToken(NameTokens.lists)
							.with(ListPresenter.LIST_PARAM, list)
							.build()));
			link.setText(list);
			trendingLists.add(link);
		}

		if (Window.getClientWidth() <= Constants.MOBILE_WIDTH_PX) {
			container.addStyleName(style.mobile());
		}
		else {
			container.removeStyleName(style.mobile());
		}
	}

	@Override
	public void hideContributeCTA() {
		contributeCTA.setVisible(false);
	}

	@Override
	public void showContributeCTA() {
		contributeCTA.setVisible(true);
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

package com.fave100.client.pages.song.widgets.whyline;

import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.WhylineProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class WhylineWidget extends Composite {

	private static WhylineWidgetUiBinder uiBinder = GWT.create(WhylineWidgetUiBinder.class);

	interface WhylineWidgetUiBinder extends UiBinder<Widget, WhylineWidget> {
	}

	@UiField Label whyline;
	@UiField InlineHyperlink userLink;

	public WhylineWidget(final WhylineProxy whylineProxy) {
		initWidget(uiBinder.createAndBindUi(this));

		whyline.setText(whylineProxy.getWhyline());

		final String userPlace = new UrlBuilder(NameTokens.users)
				.with(UsersPresenter.USER_PARAM, whylineProxy.getUsername())
				.getPlaceToken();
		userLink.setTargetHistoryToken(userPlace);
		userLink.setText(whylineProxy.getUsername());
	}

}

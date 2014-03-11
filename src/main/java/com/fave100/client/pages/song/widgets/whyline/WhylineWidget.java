package com.fave100.client.pages.song.widgets.whyline;

import com.fave100.client.generated.entities.Whyline;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.place.NameTokens;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.common.client.ClientUrlUtils;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class WhylineWidget extends Composite {

	private static WhylineWidgetUiBinder uiBinder = GWT.create(WhylineWidgetUiBinder.class);

	interface WhylineWidgetUiBinder extends UiBinder<Widget, WhylineWidget> {
	}

	@UiField Image avatar;
	@UiField Label whyline;
	@UiField InlineHyperlink userLink;

	public WhylineWidget(final Whyline whylineProxy) {
		initWidget(uiBinder.createAndBindUi(this));

		avatar.setUrl(whylineProxy.getAvatar());

		whyline.setText(whylineProxy.getWhyline());

		final String userPlace = new ParameterTokenFormatter(new ClientUrlUtils())
				.toPlaceToken(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(ListPresenter.USER_PARAM, whylineProxy.getUsername())
						.build());
		userLink.setTargetHistoryToken(userPlace);
		userLink.setText(whylineProxy.getUsername());
	}

}

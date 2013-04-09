package com.fave100.client.widgets;

import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.WhylineProxy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WhylineWidget extends Composite {

	public WhylineWidget(final WhylineProxy whyline) {
		final VerticalPanel container = new VerticalPanel();
		initWidget(container);

		final Label whylineLabel = new Label('"'+whyline.getWhyline()+'"');
		container.add(whylineLabel);

		final InlineHyperlink whylineAuthor = new InlineHyperlink();
		final String userPlace = new UrlBuilder(NameTokens.users)
			.with(UsersPresenter.USER_PARAM, whyline.getUsername())
			.getPlaceToken();
		whylineAuthor.setTargetHistoryToken(userPlace);
		whylineAuthor.setText("-"+whyline.getUsername());
		container.add(whylineAuthor);
	}

}

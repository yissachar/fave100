package com.fave100.client.widgets;

import com.fave100.client.place.NameTokens;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WhylineWidget extends Composite {

	public WhylineWidget() {
		final VerticalPanel container = new VerticalPanel();
		initWidget(container);

		final Label whyline = new Label("This is a sample Whyline");
		container.add(whyline);

		final InlineHyperlink whylineAuthor = new InlineHyperlink();
		whylineAuthor.setTargetHistoryToken(NameTokens.userlist);
		whylineAuthor.setText("AnAuthor");
		container.add(whylineAuthor);
	}

}

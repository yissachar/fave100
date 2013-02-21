package com.fave100.client.widgets;

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
		// TODO: This throws errors about infinite loop
/*		final ClientGinjector ginjector = GWT.create(ClientGinjector.class);
		final String historyToken = ginjector.getPlaceManager().buildHistoryToken(new PlaceRequest(NameTokens.users).with("u", whyline.getUsername()));
		whylineAuthor.setTargetHistoryToken(historyToken);
	*/	whylineAuthor.setText("-"+whyline.getUsername());
		container.add(whylineAuthor);
	}

}

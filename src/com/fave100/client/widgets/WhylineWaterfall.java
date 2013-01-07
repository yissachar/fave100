package com.fave100.client.widgets;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.user.client.ui.Grid;

public class WhylineWaterfall extends Grid{

	public WhylineWaterfall(final ApplicationRequestFactory requestFactory) {
		super(1, 1);

		this.setWidget(0, 0, new WhylineWidget());
	}

}

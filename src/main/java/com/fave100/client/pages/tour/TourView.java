package com.fave100.client.pages.tour;

import javax.inject.Inject;

import com.fave100.client.pages.PageView;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class TourView extends PageView<TourUiHandlers> implements TourPresenter.MyView {
	interface Binder extends UiBinder<Widget, TourView> {
	}

	@Inject
	public TourView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

}

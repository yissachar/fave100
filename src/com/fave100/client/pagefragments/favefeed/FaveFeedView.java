package com.fave100.client.pagefragments.favefeed;

import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class FaveFeedView extends ViewImpl implements FaveFeedPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, FaveFeedView> {
	}

	@Inject
	public FaveFeedView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
}

package com.fave100.client.pages.lists.widgets.globallistdetails;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class GlobalListDetailsView extends ViewImpl implements GlobalListDetailsPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, GlobalListDetailsView> {
	}

	@UiField Label hashtagLabel;

	@Inject
	public GlobalListDetailsView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInfo(final String hashtag) {
		hashtagLabel.setText(hashtag);
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

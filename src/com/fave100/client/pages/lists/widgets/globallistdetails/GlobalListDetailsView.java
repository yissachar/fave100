package com.fave100.client.pages.lists.widgets.globallistdetails;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class GlobalListDetailsView extends ViewImpl implements GlobalListDetailsPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, GlobalListDetailsView> {
	}

	@UiField Label hashtagLabel;
	@UiField HTMLPanel listAutocomplete;

	@Inject
	public GlobalListDetailsView(final Binder binder) {
		widget = binder.createAndBindUi(this);
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

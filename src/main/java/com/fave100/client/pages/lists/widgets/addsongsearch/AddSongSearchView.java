package com.fave100.client.pages.lists.widgets.addsongsearch;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

public class AddSongSearchView extends PopupViewWithUiHandlers<AddSongSearchUiHandlers> implements AddSongSearchPresenter.MyView {
	public interface Binder extends UiBinder<PopupPanel, AddSongSearchView> {
	}

	@UiField HTMLPanel search;

	@Inject
	AddSongSearchView(Binder uiBinder, EventBus eventBus) {
		super(eventBus);

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (content == null)
			return;

		if (slot == AddSongSearchPresenter.SEARCH_SLOT) {
			search.clear();
			search.add(content);
		}
	}
}

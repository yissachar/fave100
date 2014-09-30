package com.fave100.client.widgets.searchpopup;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

public class PopupSearchView extends PopupViewWithUiHandlers<PopupSearchUiHandlers> implements PopupSearchPresenter.MyView {
	public interface Binder extends UiBinder<PopupPanel, PopupSearchView> {
	}

	@UiField HTMLPanel search;

	@Inject
	PopupSearchView(Binder uiBinder, EventBus eventBus) {
		super(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (content == null)
			return;

		if (slot == PopupSearchPresenter.SEARCH_SLOT) {
			search.clear();
			search.add(content);
		}
	}

	@Override
	public void reposition() {
		center();
	}

}

package com.fave100.client.pages.users.widgets.listmanager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ListManagerView extends ViewWithUiHandlers<ListManagerUiHandlers> implements ListManagerPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, ListManagerView> {
	}

	@UiField TextBox newHashtag;
	@UiField Button addHashtagButton;

	@Inject
	public ListManagerView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("addHashtagButton")
	void onAddHashtagClick(final ClickEvent event) {
		getUiHandlers().addHashtag(newHashtag.getText());
	}
}

package com.fave100.client.pages.users.widgets.listmanager;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
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
	@UiField FlowPanel lists;
	@UiField Label errorMsg;

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

	@Override
	public void refreshList(final List<FlowPanel> panels) {
		lists.clear();
		for (final FlowPanel panel : panels) {
			lists.add(panel);
		}
	}

	@Override
	public void showError(final String msg) {
		errorMsg.setText(msg);
		errorMsg.setVisible(true);
	}

	@Override
	public void hideError() {
		errorMsg.setVisible(false);
	}
}

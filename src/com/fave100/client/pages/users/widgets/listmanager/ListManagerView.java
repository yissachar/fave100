package com.fave100.client.pages.users.widgets.listmanager;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ListManagerView extends ViewWithUiHandlers<ListManagerUiHandlers> implements ListManagerPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, ListManagerView> {
	}

	@UiField FocusPanel currentListContainer;
	@UiField Label currentList;
	@UiField FlowPanel listDropdown;
	@UiField TextBox searchBox;
	@UiField FlowPanel listContainer;
	@UiField Label noMatchesLabel;
	@UiField Label errorMsg;
	@UiField ListManagerStyle style;
	int selectedIndex = 0;

	interface ListManagerStyle extends CssResource {
		String selected();

		String dropdownVisible();
	}

	@Inject
	public ListManagerView(final Binder binder) {
		widget = binder.createAndBindUi(this);
		listDropdown.setVisible(false);
		noMatchesLabel.setVisible(false);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("currentListContainer")
	void onCurrentListClick(final ClickEvent event) {
		listDropdown.setVisible(!listDropdown.isVisible());
		if (listDropdown.isVisible()) {
			currentListContainer.addStyleName(style.dropdownVisible());
			searchBox.setFocus(true);
		}
		else {
			currentListContainer.removeStyleName(style.dropdownVisible());
		}
	}

	@UiHandler("searchBox")
	void onSearchBoxKeyUp(final KeyUpEvent event) {
		boolean matchFound = false;
		for (int i = 0; i < listContainer.getWidgetCount(); i++) {
			final Label list = (Label)listContainer.getWidget(i);
			if (list.getText().startsWith(searchBox.getText()) || searchBox.getText().isEmpty()) {
				list.setVisible(true);
				matchFound = true;
			}
			else {
				list.setVisible(false);
			}
		}
		if (!matchFound)
			noMatchesLabel.setVisible(true);
		else
			noMatchesLabel.setVisible(false);
	}

	@Override
	public void refreshList(final List<String> lists, final String selected) {
		listContainer.clear();
		currentListContainer.removeStyleName(style.dropdownVisible());
		int i = 0;
		for (final String list : lists) {
			final Label label = new Label(list);
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					getUiHandlers().listChanged(list);
					listDropdown.setVisible(false);
					currentListContainer.removeStyleName(style.dropdownVisible());
				}
			});
			listContainer.add(label);
			if (list.equals(selected)) {
				label.addStyleName("selected");
				selectedIndex = i;
			}
			i++;
		}

		currentList.setText(selected);

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

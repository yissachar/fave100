package com.fave100.client.pages.lists.widgets.listmanager;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ListManagerView extends ViewWithUiHandlers<ListManagerUiHandlers> implements ListManagerPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, ListManagerView> {
	}

	@UiField FocusPanel currentListContainer;
	@UiField Label currentList;
	@UiField FlowPanel listDropdown;
	@UiField TextBox listNameInput;
	@UiField Button addHashtagButton;
	@UiField FlowPanel addListContainer;
	@UiField FlowPanel listContainer;
	@UiField Label errorMsg;
	@UiField ListManagerStyle style;
	int selectedIndex = 0;
	private HandlerRegistration rootClickHandler;

	interface ListManagerStyle extends CssResource {
		String selected();

		String dropdownVisible();
	}

	@Inject
	public ListManagerView(final Binder binder) {
		widget = binder.createAndBindUi(this);
		listDropdown.setVisible(false);
		listNameInput.setVisible(false);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("currentListContainer")
	void onCurrentListClick(final ClickEvent event) {
		hideError();
		if (listDropdown.isVisible()) {
			hideDropdown();
		}
		else {
			listDropdown.setVisible(true);
			currentListContainer.addStyleName(style.dropdownVisible());
			final ClickHandler clickHandler = new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					boolean shouldHide = true;
					final Element target = Element.as(event.getNativeEvent().getEventTarget());

					if (widgetContainsElement(currentListContainer, target))
						shouldHide = false;

					if (widgetContainsElement(addListContainer, target))
						shouldHide = false;

					if (shouldHide)
						hideDropdown();
				}
			};
			rootClickHandler = RootPanel.get().addDomHandler(clickHandler, ClickEvent.getType());
		}
	}

	private boolean widgetContainsElement(final Widget widget, final Element element) {
		if (widget instanceof HasWidgets) {
			final Iterator<Widget> iter = ((HasWidgets)widget).iterator();
			while (iter.hasNext()) {
				final Widget child = iter.next();
				if ((child != widget && widgetContainsElement(child, element)) || element.equals(child.getElement()) || element.equals(widget.getElement()))
					return true;
			}
		}
		return false;
	}

	@UiHandler("listNameInput")
	void onListNameKeyUp(final KeyUpEvent event) {
		if (KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
			getUiHandlers().addHashtag(listNameInput.getText());
			hideDropdown();
			listNameInput.setVisible(false);
			addHashtagButton.setVisible(true);
		}
		else if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
			listNameInput.setText("");
			listNameInput.setVisible(false);
			addHashtagButton.setVisible(true);
		}
	}

	@UiHandler("listNameInput")
	void onListNameBlur(final BlurEvent event) {
		listNameInput.setText("");
		listNameInput.setVisible(false);
		addHashtagButton.setVisible(true);
	}

	@UiHandler("addHashtagButton")
	void onAddButtonClick(final ClickEvent event) {
		listNameInput.setVisible(true);
		listNameInput.setFocus(true);
		addHashtagButton.setVisible(false);
	}

	@Override
	public void refreshList(final List<String> lists, final String selected) {
		listContainer.clear();
		int i = 0;
		for (final String list : lists) {
			final Label label = new Label(list);
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					getUiHandlers().listChanged(list);
					hideDropdown();
				}
			});
			listContainer.add(label);
			if (list.equals(selected)) {
				label.addStyleName("selected");
				selectedIndex = i;
			}
			i++;
		}

		// Hide the whole widget if only has one list
		if (i <= 1)
			widget.setVisible(false);
		else
			widget.setVisible(true);

		currentList.setText(selected);
	}

	@Override
	public void hideDropdown() {
		listDropdown.setVisible(false);
		currentListContainer.removeStyleName(style.dropdownVisible());
		listNameInput.setText("");
		if (rootClickHandler != null)
			rootClickHandler.removeHandler();
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

	@Override
	public void setOwnList(final boolean ownList) {
		if (ownList) {
			addListContainer.setVisible(true);
		}
		else {
			addListContainer.setVisible(false);
		}
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

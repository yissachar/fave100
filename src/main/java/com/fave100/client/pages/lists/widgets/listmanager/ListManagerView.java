package com.fave100.client.pages.lists.widgets.listmanager;

import java.util.List;

import com.fave100.client.Utils;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.client.widgets.welcomeinfo.WelcomeInfo;
import com.fave100.shared.ListMode;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class ListManagerView extends ViewWithUiHandlers<ListManagerUiHandlers> implements ListManagerPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, ListManagerView> {
	}

	@UiField FocusPanel currentListContainer;
	@UiField Label currentList;
	@UiField FlowPanel listDropdown;
	@UiField Image dropdownToggle;
	@UiField Panel userCriticToggle;
	@UiField Hyperlink usersLink;
	@UiField Hyperlink criticsLink;
	@UiField HTMLPanel autocomplete;
	@UiField Button addHashtagButton;
	@UiField FlowPanel addListContainer;
	@UiField FlowPanel listContainer;
	@UiField Label errorMsg;
	@UiField Hyperlink globalListLink;
	@UiField WelcomeInfo welcomeInfo;
	@UiField ListManagerStyle style;
	int selectedIndex = 0;
	private HandlerRegistration rootClickHandler;
	private ParameterTokenFormatter _tokenFormatter;

	interface ListManagerStyle extends GlobalStyle {
		String dropdownVisible();

		String listName();

		String deleteButton();

		String selected();
	}

	@Inject
	public ListManagerView(final Binder binder, ParameterTokenFormatter tokenFormatter, EventBus eventBus) {
		widget = binder.createAndBindUi(this);
		_tokenFormatter = tokenFormatter;
		autocomplete.setVisible(false);
		listDropdown.setVisible(false);
		userCriticToggle.setVisible(false);
		welcomeInfo.setEventBus(eventBus);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {

		if (slot == ListManagerPresenter.AUTOCOMPLETE_SLOT) {
			autocomplete.clear();
			if (content != null) {
				autocomplete.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@UiHandler("currentListContainer")
	void onCurrentListClick(final ClickEvent event) {
		if (!dropdownToggle.isVisible())
			return;

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

					final Element target = Element.as(event.getNativeEvent().getEventTarget());

					boolean shouldHide = !Utils.widgetContainsElement(currentListContainer, target)
							&& !Utils.widgetContainsElement(addListContainer, target)
							&& !Utils.widgetContainsElement(autocomplete, target);

					if (shouldHide) {
						hideDropdown();
					}
				}
			};
			rootClickHandler = RootPanel.get().addDomHandler(clickHandler, ClickEvent.getType());
		}
	}

	@UiHandler("addHashtagButton")
	void onAddButtonClick(final ClickEvent event) {
		addHashtagButton.setVisible(false);
		autocomplete.setVisible(true);
		listContainer.setVisible(false);
		getUiHandlers().setAutocompleteFocus(true);
	}

	@Override
	public void refreshList(final List<String> lists, final String selected, boolean ownList) {
		listContainer.clear();
		int i = 0;
		for (final String list : lists) {
			final FlowPanel listItemContainer = new FlowPanel();

			final InlineLabel label = new InlineLabel(list);
			label.addStyleName(style.listName());
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					getUiHandlers().listChanged(list);
					hideDropdown();
				}
			});
			listItemContainer.add(label);

			if (ownList) {
				final InlineLabel deleteButton = new InlineLabel("✖");
				deleteButton.addStyleName(style.deleteButton());
				deleteButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						getUiHandlers().deleteList(list);
					}
				});
				listItemContainer.add(deleteButton);
			}

			listContainer.add(listItemContainer);

			if (list.equals(selected)) {
				label.addStyleName("selected");

				selectedIndex = i;
			}

			i++;
		}

		dropdownToggle.setVisible(lists.size() > 1 || ownList);

		currentList.setText(selected);
		usersLink.setTargetHistoryToken(_tokenFormatter.toPlaceToken(
				new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.LIST_PARAM, selected)
						.build()));

		criticsLink.setTargetHistoryToken(_tokenFormatter.toPlaceToken(
				new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.LIST_PARAM, selected)
						.with(PlaceParams.MODE_PARAM, ListMode.CRITICS)
						.build()));
	}

	@Override
	public void hideDropdown() {
		listDropdown.setVisible(false);
		listContainer.setVisible(true);
		currentListContainer.removeStyleName(style.dropdownVisible());
		addHashtagButton.setVisible(true);
		autocomplete.setVisible(false);
		getUiHandlers().clearSearch();
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

	@Override
	public void showUserCriticToggle(boolean show) {
		userCriticToggle.setVisible(show);
	}

	@Override
	public void setListMode(String listMode) {
		criticsLink.removeStyleName(style.selected());
		usersLink.removeStyleName(style.selected());

		if (ListMode.CRITICS.equals(listMode)) {
			criticsLink.addStyleName(style.selected());
		}
		else if (ListMode.ALL.equals(listMode)) {
			usersLink.addStyleName(style.selected());
		}
	}

	@Override
	public void showWelcomeInfo(boolean show) {
		welcomeInfo.setVisible(show);
	}
}

package com.fave100.client.pages.lists.widgets.listmanager;

import java.util.List;

import com.fave100.client.events.favelist.ListAddedEvent;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.pages.lists.widgets.autocomplete.list.ListAutocompletePresenter;
import com.fave100.client.rest.RestSessionDispatch;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class AddListAutocompletePresenter extends ListAutocompletePresenter {

	public static final String NEW_LIST_PROMPT = "Create new list: ";

	@Inject
	AddListAutocompletePresenter(final EventBus eventBus, final MyView view, final RestSessionDispatch dispatcher, final RestServiceFactory restServiceFactory) {
		super(eventBus, view, dispatcher, restServiceFactory);
	}

	@Override
	public void listSelected() {
		if (getSelection() < 0)
			return;

		String selection = _suggestions.get(getSelection());
		if (selection.startsWith(NEW_LIST_PROMPT))
			selection = selection.substring(NEW_LIST_PROMPT.length(), selection.length());

		super.listSelected();

		_eventBus.fireEvent(new ListAddedEvent(selection));
	}

	@Override
	public void setSuggestions(List<String> suggestions) {
		if (!suggestions.contains(_lastSearch)) {
			suggestions.add(NEW_LIST_PROMPT + _lastSearch);
		}
		super.setSuggestions(suggestions);
	}

}
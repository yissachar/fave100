package com.fave100.client.pages.lists.widgets.listmanager;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.favelist.ListAddedEvent;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.widgets.autocomplete.AutocompletePresenter;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

public class AddListAutocompletePresenter extends AutocompletePresenter {

	public static final String NEW_LIST_PROMPT = "Create new list: ";

	@Inject
	AddListAutocompletePresenter(final EventBus eventBus, final MyView view, final PlaceManager placeManager, final RestDispatchAsync dispatcher, final RestServiceFactory restServiceFactory,
									final CurrentUser currentUser) {
		super(eventBus, view, placeManager, dispatcher, restServiceFactory, currentUser);
		setPlaceholder("Search lists...");
	}

	@Override
	public void getAutocompleteResults(String searchTerm) {
		_action = _restServiceFactory.search().searchFaveLists(searchTerm, null);
		super.getAutocompleteResults(searchTerm);
	}

	@Override
	public void suggestionSelected() {
		if (getSelection() < 0)
			return;

		String selection = _suggestions.get(getSelection());
		if (selection.startsWith(NEW_LIST_PROMPT))
			selection = selection.substring(NEW_LIST_PROMPT.length(), selection.length());

		super.suggestionSelected();

		_eventBus.fireEvent(new ListAddedEvent(selection));
	}

	@Override
	public void setSuggestions(List<String> suggestions) {
		// Compare lower case suggestions to determine if in list already or not
		List<String> lcSuggestions = new ArrayList<>();
		for (String suggestion : suggestions) {
			lcSuggestions.add(suggestion.toLowerCase());
		}

		if (_lastSearch != null && !lcSuggestions.contains(_lastSearch.toLowerCase())) {
			suggestions.add(NEW_LIST_PROMPT + _lastSearch);
		}

		super.setSuggestions(suggestions);
	}

}

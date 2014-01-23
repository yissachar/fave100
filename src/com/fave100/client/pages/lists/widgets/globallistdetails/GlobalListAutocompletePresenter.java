package com.fave100.client.pages.lists.widgets.globallistdetails;

import com.fave100.client.events.favelist.ListChangedEvent;
import com.fave100.client.generated.services.FaveListService;
import com.fave100.client.pages.lists.widgets.autocomplete.list.ListAutocompletePresenter;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;

public class GlobalListAutocompletePresenter extends ListAutocompletePresenter {

	@Inject
	GlobalListAutocompletePresenter(final EventBus eventBus, final MyView view, final RestDispatchAsync dispatcher,
									final FaveListService faveListService) {
		super(eventBus, view, dispatcher, faveListService);
	}

	@Override
	public void listSelected() {
		if (getSelection() < 0)
			return;

		String selection = _suggestions.get(getSelection());

		super.listSelected();

		_eventBus.fireEvent(new ListChangedEvent(selection));
	}

}

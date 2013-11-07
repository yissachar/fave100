package com.fave100.client.pages.lists.widgets.globallistdetails;

import com.fave100.client.events.favelist.ListAddedEvent;
import com.fave100.client.pages.lists.widgets.autocomplete.list.ListAutocompletePresenter;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class GlobalListAutocompletePresenter extends ListAutocompletePresenter {

	@Inject
	GlobalListAutocompletePresenter(final EventBus eventBus, final MyView view, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view, requestFactory);
	}

	@Override
	public void listSelected() {
		if (getSelection() < 0)
			return;

		String selection = _suggestions.get(getSelection());

		super.listSelected();

		_eventBus.fireEvent(new ListAddedEvent(selection));
	}

}

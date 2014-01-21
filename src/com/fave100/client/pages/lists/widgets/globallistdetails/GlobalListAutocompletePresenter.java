package com.fave100.client.pages.lists.widgets.globallistdetails;

import com.fave100.client.events.favelist.ListChangedEvent;
import com.fave100.client.generated.services.FaveListService;
import com.fave100.client.pages.lists.widgets.autocomplete.list.ListAutocompletePresenter;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;

public class GlobalListAutocompletePresenter extends ListAutocompletePresenter {

	@Inject
	GlobalListAutocompletePresenter(final EventBus eventBus, final MyView view, final ApplicationRequestFactory requestFactory, final DispatchAsync dispatcher,
									final FaveListService faveListService) {
		super(eventBus, view, requestFactory, dispatcher, faveListService);
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

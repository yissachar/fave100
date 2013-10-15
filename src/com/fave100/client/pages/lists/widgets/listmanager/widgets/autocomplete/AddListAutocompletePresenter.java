package com.fave100.client.pages.lists.widgets.listmanager.widgets.autocomplete;

import java.util.LinkedList;
import java.util.List;

import com.fave100.client.events.favelist.ListAddedEvent;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class AddListAutocompletePresenter extends PresenterWidget<AddListAutocompletePresenter.MyView> implements AddListAutocompleteUiHandlers {

	public interface MyView extends View, HasUiHandlers<AddListAutocompleteUiHandlers> {

		void setSuggestions(List<String> suggestions);

		void setSelection(int selection);

		void clearSearch();

		void setFocus(boolean focus);
	}

	public static final String NEW_LIST_PROMPT = "Create new list: ";
	private EventBus _eventBus;
	private ApplicationRequestFactory _requestFactory;
	private final List<Request<List<String>>> _requests;
	private List<String> _suggestions;
	private String _lastSearch;
	private int _selection = 0;
	private int _maxSelection = -1;

	@Inject
	AddListAutocompletePresenter(final EventBus eventBus, final MyView view, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view);
		_eventBus = eventBus;
		_requestFactory = requestFactory;
		_requests = new LinkedList<Request<List<String>>>();
		getAutocompleteResults("");
		getView().setUiHandlers(this);
	}

	// Get global lists matching a search term
	@Override
	public void getAutocompleteResults(String _searchTerm) {
		// Don't bother going to the server a second time for the same term
		if (_lastSearch != null && _lastSearch.equals(_searchTerm))
			return;

		_lastSearch = _searchTerm;

		final String searchTerm = _searchTerm.trim();
		if (searchTerm.isEmpty()) {
			setSelection(-1);
			getView().setSuggestions(null);
			return;
		}

		final Request<List<String>> listAutocompleteReq = _requestFactory.faveListRequest().getHashtagAutocomplete(searchTerm);
		listAutocompleteReq.fire(new Receiver<List<String>>() {
			@Override
			public void onSuccess(final List<String> suggestions) {
				if (_requests.indexOf(listAutocompleteReq) != _requests.size() - 1
						|| _requests.indexOf(listAutocompleteReq) == -1) {
					_requests.remove(listAutocompleteReq);
					return;
				}

				_requests.clear();

				_suggestions = suggestions;
				if (!suggestions.contains(searchTerm)) {
					_suggestions.add(NEW_LIST_PROMPT + searchTerm);
				}
				getView().setSuggestions(suggestions);
				setMaxSelection(suggestions == null ? 0 : suggestions.size() - 1);
				setSelection(0);
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				_requests.remove(this);
			}
		});
		_requests.add(listAutocompleteReq);
	}

	// Set the currently selected list
	@Override
	public void setSelection(final int position, final boolean relative) {
		final int newSelection = relative ? getSelection() + position : position;
		if (newSelection >= 0 && newSelection <= getMaxSelection()) {
			setSelection(newSelection);
		}
	}

	// Dispatch a list selected event and hide current results
	@Override
	public void listSelected() {
		if (getSelection() < 0)
			return;

		String selection = _suggestions.get(getSelection());
		if (selection.startsWith(NEW_LIST_PROMPT))
			selection = selection.substring(NEW_LIST_PROMPT.length(), selection.length());
		_eventBus.fireEvent(new ListAddedEvent(selection));
		getView().clearSearch();
	}

	/* Getters and Setters */

	@Override
	public int getSelection() {
		return _selection;
	}

	public void setSelection(final int selection) {
		_selection = selection;
		getView().setSelection(_selection);
	}

	public int getMaxSelection() {
		return _maxSelection;
	}

	public void setMaxSelection(final int maxSelection) {
		_maxSelection = maxSelection;
	}

}

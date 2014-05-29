package com.fave100.client.widgets.autocomplete;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.generated.entities.CursoredSearchResult;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.generated.services.RestServiceFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

public class AutocompletePresenter extends PresenterWidget<AutocompletePresenter.MyView> implements AutocompleteUiHandlers {

	public interface MyView extends View, HasUiHandlers<AutocompleteUiHandlers> {

		void setSuggestions(List<String> suggestions);

		void setSelection(int selection);

		void clearSearch();

		void setFocus(boolean focused);

		void setPlaceholder(String placeholder);
	}

	protected EventBus _eventBus;
	protected PlaceManager _placeManager;
	protected RestDispatchAsync _dispatcher;
	protected RestServiceFactory _restServiceFactory;
	protected CurrentUser _currentUser;
	protected List<String> _suggestions;
	protected String _lastSearch;
	protected RestAction<CursoredSearchResult> _action;
	private final List<AsyncCallback<CursoredSearchResult>> _requests;
	private int _selection = 0;
	private int _maxSelection = -1;

	@Inject
	public AutocompletePresenter(final EventBus eventBus, final MyView view, final PlaceManager placeManager, final RestDispatchAsync dispatcher,
									final RestServiceFactory restServiceFactory, final CurrentUser currentUser) {
		super(eventBus, view);
		_eventBus = eventBus;
		_dispatcher = dispatcher;
		_placeManager = placeManager;
		_restServiceFactory = restServiceFactory;
		_currentUser = currentUser;
		_requests = new LinkedList<AsyncCallback<CursoredSearchResult>>();
		getAutocompleteResults("");
		getView().setUiHandlers(this);
	}

	// Get suggestions matching a search term
	@Override
	public void getAutocompleteResults(String searchTerm) {
		// Don't bother going to the server a second time for the same term
		if (_lastSearch != null && _lastSearch.equals(searchTerm))
			return;

		_lastSearch = searchTerm;

		searchTerm = searchTerm.trim();
		if (searchTerm.isEmpty()) {
			setSelection(-1);
			getView().setSuggestions(null);
			return;
		}

		final AsyncCallback<CursoredSearchResult> autocompleteReq = new AsyncCallback<CursoredSearchResult>() {

			@Override
			public void onFailure(Throwable caught) {
				_requests.remove(this);
			}

			@Override
			public void onSuccess(CursoredSearchResult result) {
				if (_requests.indexOf(this) != _requests.size() - 1
						|| _requests.indexOf(this) == -1) {
					_requests.remove(this);
					return;
				}

				_requests.clear();
				List<String> suggestions = new ArrayList<>();
				for (StringResult stringResult : result.getSearchResults().getItems()) {
					suggestions.add(stringResult.getValue());
				}
				setSuggestions(suggestions);
			}
		};

		_dispatcher.execute(_action, autocompleteReq);
		_requests.add(autocompleteReq);
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
	public void suggestionSelected() {
		if (getSelection() < 0)
			return;

		getAutocompleteResults("");
		getView().clearSearch();
	}

	public void setSuggestions(List<String> suggestions) {
		_suggestions = suggestions;
		getView().setSuggestions(suggestions);
		setMaxSelection(suggestions == null ? 0 : suggestions.size() - 1);
		setSelection(0);
	}

	public void setPlaceholder(String placeholder) {
		getView().setPlaceholder(placeholder);
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

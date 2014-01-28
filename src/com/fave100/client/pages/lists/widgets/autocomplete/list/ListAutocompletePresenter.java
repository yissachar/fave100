package com.fave100.client.pages.lists.widgets.autocomplete.list;

import java.util.LinkedList;
import java.util.List;

import com.fave100.client.generated.entities.StringCollection;
import com.fave100.client.generated.services.FaveListService;
import com.fave100.client.rest.RestSessionDispatch;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class ListAutocompletePresenter extends PresenterWidget<ListAutocompletePresenter.MyView> implements ListAutocompleteUiHandlers {

	public interface MyView extends View, HasUiHandlers<ListAutocompleteUiHandlers> {

		void setSuggestions(List<String> suggestions);

		void setSelection(int selection);

		void clearSearch();

		void setFocus(boolean focused);
	}

	protected EventBus _eventBus;
	protected RestSessionDispatch _dispatcher;
	protected FaveListService _faveListService;
	protected List<String> _suggestions;
	protected String _lastSearch;
	private final List<AsyncCallback<StringCollection>> _requests;
	private int _selection = 0;
	private int _maxSelection = -1;

	@Inject
	public ListAutocompletePresenter(final EventBus eventBus, final MyView view, final RestSessionDispatch dispatcher,
										final FaveListService faveListService) {
		super(eventBus, view);
		_eventBus = eventBus;
		_dispatcher = dispatcher;
		_faveListService = faveListService;
		_requests = new LinkedList<AsyncCallback<StringCollection>>();
		getAutocompleteResults("");
		getView().setUiHandlers(this);
	}

	// Get global lists matching a search term
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

		final AsyncCallback<StringCollection> listAutocompleteReq = new AsyncCallback<StringCollection>() {

			@Override
			public void onFailure(Throwable caught) {
				_requests.remove(this);
			}

			@Override
			public void onSuccess(StringCollection result) {
				if (_requests.indexOf(this) != _requests.size() - 1
						|| _requests.indexOf(this) == -1) {
					_requests.remove(this);
					return;
				}

				_requests.clear();
				setSuggestions(result.getItems());
			}
		};

		_dispatcher.execute(_faveListService.getHashtagAutocomplete(searchTerm), listAutocompleteReq);
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

		getAutocompleteResults("");
		getView().clearSearch();
	}

	public void setSuggestions(List<String> suggestions) {
		_suggestions = suggestions;
		getView().setSuggestions(suggestions);
		setMaxSelection(suggestions == null ? 0 : suggestions.size() - 1);
		setSelection(0);
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

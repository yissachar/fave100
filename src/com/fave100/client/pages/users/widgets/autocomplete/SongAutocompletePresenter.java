package com.fave100.client.pages.users.widgets.autocomplete;

import java.util.LinkedList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.favelist.FaveListSizeChangedEvent;
import com.fave100.client.events.song.SongSelectedEvent;
import com.fave100.shared.Constants;
import com.fave100.shared.requestfactory.SearchResultProxy;
import com.fave100.shared.requestfactory.SongProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;

public class SongAutocompletePresenter extends
		PresenterWidget<SongAutocompletePresenter.MyView>
		implements SongAutocompleteUiHandlers {

	public interface MyView extends View, HasUiHandlers<SongAutocompleteUiHandlers> {
		void setSuggestions(List<SongProxy> suggestions, int total);

		void setSelection(int selection);

		void showPrevious(boolean show);

		void showNext(boolean show);

		void clearSearch();

		void showHelp();

		void hideHelp();

		String getSearchTerm();

		void resizeSearch();

		void showBackToTop(boolean show);

		void setFocus();

		void setSearchError(String error);
	}

	private final EventBus eventBus;
	private CurrentUser _currentUser;
	private final List<AsyncCallback<JavaScriptObject>> requests;
	private int selection = 0;
	private int maxSelection = -1;
	private int resultsPerPage = 5;
	private int page = 0;
	private List<SongProxy> currentSuggestions;
	private String _lastSearch = "";

	@Inject
	public SongAutocompletePresenter(final EventBus eventBus, final MyView view, final CurrentUser currentUser) {
		super(eventBus, view);
		this.eventBus = eventBus;
		_currentUser = currentUser;
		requests = new LinkedList<AsyncCallback<JavaScriptObject>>();
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		FaveListSizeChangedEvent.register(eventBus, new FaveListSizeChangedEvent.Handler() {
			@Override
			public void onFaveListLoaded(final FaveListSizeChangedEvent event) {
				if (event.getSize() == 0 && _currentUser.getCurrentHashtag().equals(Constants.DEFAULT_HASHTAG)) {
					getView().showHelp();
				}
				else {
					getView().hideHelp();
				}
			}
		});

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				getView().resizeSearch();
			}
		});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		getAutocompleteResults("", true);
		getView().resizeSearch();
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().clearSearch();
	}

	public interface SearchResultFactory extends AutoBeanFactory {
		AutoBean<SearchResultProxy> response();
	}

	public void setFocus() {
		getView().setFocus();
	}

	// Get a list of songs matching a search term
	@Override
	public void getAutocompleteResults(String searchTerm, final boolean resetPage) {
		// Don't bother going to the server a second time for the same term
		if (_lastSearch != null && _lastSearch.equals(searchTerm))
			return;

		_lastSearch = searchTerm;

		if (resetPage)
			setPage(0);

		setSelection(-1);
		searchTerm = searchTerm.trim();
		if (searchTerm.isEmpty() || searchTerm.length() <= 2) {
			getView().setSuggestions(null, 0);
			return;
		}

		final String url = Constants.SEARCH_URL + "searchTerm=" + URL.encodeQueryString(searchTerm) + "&limit=5&page=" + page;
		final AsyncCallback<JavaScriptObject> autocompleteReq = new AsyncCallback<JavaScriptObject>() {
			@Override
			public void onFailure(final Throwable caught) {
				requests.remove(this);
				getView().setSearchError("Error");
			}

			@Override
			public void onSuccess(final JavaScriptObject jsObject) {
				if (requests.indexOf(this) != requests.size() - 1
						|| requests.indexOf(this) == -1) {
					requests.remove(this);
					return;
				}

				requests.clear();

				final JSONObject obj = new JSONObject(jsObject);
				final SearchResultFactory factory = GWT.create(SearchResultFactory.class);
				final AutoBean<SearchResultProxy> autoBean = AutoBeanCodex.decode(factory, SearchResultProxy.class, obj.toString());
				final List<SongProxy> results = autoBean.as().getResults();
				currentSuggestions = results;
				final int totalResults = Integer.parseInt(obj.get("total").toString());
				getView().setSuggestions(results, totalResults);
				getView().hideHelp();
				setMaxSelection(results.size() - 1);

				if (getPage() == 0)
					getView().showPrevious(false);
				else
					getView().showPrevious(true);

				if (resultsPerPage * getPage() + results.size() >= totalResults)
					getView().showNext(false);
				else
					getView().showNext(true);

				getView().resizeSearch();

				if (!resetPage)
					setSelection(0, false);

			}
		};
		requests.add(autocompleteReq);
		final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(url, autocompleteReq);
	}

	// Set the currently selected song
	@Override
	public void setSelection(final int position, final boolean relative) {
		final int newSelection = relative ? getSelection() + position : position;
		if (newSelection >= -1 && newSelection <= getMaxSelection()) {
			setSelection(newSelection);
			getView().setSelection(selection);
		}
	}

	// Dispatch a song selected event and hide current results
	@Override
	public void songSelected() {
		if (getSelection() < 0)
			return;
		eventBus.fireEvent(new SongSelectedEvent(currentSuggestions.get(getSelection())));
		getAutocompleteResults("", true);
		getView().clearSearch();
	}

	// Change the page number and show the results for the new page
	@Override
	public void modifyPage(final int pageModifier) {
		final int newPage = getPage() + pageModifier;
		if (newPage >= 0) {
			setPage(newPage);
			getAutocompleteResults(getView().getSearchTerm(), false);
		}
	}

	/* Getters and Setters */

	@Override
	public int getSelection() {
		return selection;
	}

	public void setSelection(final int selection) {
		this.selection = selection;
	}

	public int getMaxSelection() {
		return maxSelection;
	}

	public void setMaxSelection(final int maxSelection) {
		this.maxSelection = maxSelection;
	}

	@Override
	public int getPage() {
		return page;
	}

	public void setPage(final int page) {
		this.page = page;
	}

	public int getResultsPerPage() {
		return resultsPerPage;
	}

	public void setResultsPerPage(final int resultsPerPage) {
		this.resultsPerPage = resultsPerPage;
	}

}

interface SongAutocompleteUiHandlers extends UiHandlers {
	void getAutocompleteResults(String searchTerm, boolean resetPage);

	int getSelection();

	void setSelection(int position, boolean relative);

	void songSelected();

	void modifyPage(int pageModifier);

	int getPage();
}
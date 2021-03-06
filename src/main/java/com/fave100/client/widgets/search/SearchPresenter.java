package com.fave100.client.widgets.search;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.FaveApi;
import com.fave100.client.entities.SearchResult;
import com.fave100.client.entities.SearchResultMapper;
import com.fave100.client.entities.SongDto;
import com.fave100.client.generated.entities.CursoredSearchResult;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.pagefragments.popups.addsong.AddSongPresenter;
import com.fave100.shared.Constants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class SearchPresenter extends PresenterWidget<SearchPresenter.MyView> implements SearchUiHandlers {

	public interface MyView extends View, HasUiHandlers<SearchUiHandlers> {
		void setSongSuggestions(List<SongDto> songs, boolean loadMore);

		void setStringSuggestions(List<String> suggestions, boolean loadMore);

		void setSongTypeSuggestions(List<SearchType> suggestions);

		void setSelectedSearchType(SearchType searchType);

		void setSingleSearch(boolean singleSearch);

		void focus();

		void setDarkText(boolean darkText);

		void setFullPageSearch(boolean fullPage);

		void setPlaceHolder(String placeHolder);
	}

	public final static int SELECTIONS_PER_PAGE = 5;
	public final static int ITUNES_SEARCH_LIMIT = 30;

	private int _page = 0;
	private int _selection = -1;
	private int _maxSelection = 0;
	private String _lastSearchTerm = "";
	private String _cursor;
	private SuggestionSelectedAction _selectedAction;
	private SearchCompletedAction _completedAction;
	private SearchType _searchType = SearchType.SONGS;
	private final List<AsyncCallback<?>> _currentRequests = new ArrayList<>();
	private List<?> _currentSuggestions;
	private FaveApi _api;
	@Inject AddSongPresenter _addSongPresenter;

	@Inject
	SearchPresenter(EventBus eventBus, MyView view, FaveApi api) {
		super(eventBus, view);
		_api = api;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		setSearchType(SearchType.SONGS);
	}

	@Override
	public void getSearchResults(String searchTerm) {
		clearSearchResults();
		getSearchResults(searchTerm, false);
	}

	public void getSearchResults(String searchTerm, boolean loadMore) {
		searchTerm = searchTerm.trim();

		if (!searchTerm.equals(_lastSearchTerm)) {
			clearSearchResults();
		}

		if (searchTerm.isEmpty()) {
			clearSearchResults();

			if (_searchType == null) {
				getSongTypeSuggestions();
			}
		}
		else if (_searchType != null) {
			switch (_searchType) {
				case SONGS:
					getSongSearchResults(searchTerm, loadMore);
					break;

				case USERS:
				case LISTS:
					getStringSearchResults(searchTerm, loadMore);
					break;

				default:
					break;
			}
		}
		_lastSearchTerm = searchTerm;
	}

	private void getSongSearchResults(final String searchTerm, final boolean loadMore) {
		if (searchTerm.length() <= 2) {
			clearSearchResults();
			return;
		}

		final String url = Constants.SEARCH_URL + "searchTerm=" + URL.encodeQueryString(searchTerm) + "&limit=" + SELECTIONS_PER_PAGE + "&page=" + _page;
		final AsyncCallback<JavaScriptObject> autocompleteReq = new AsyncCallback<JavaScriptObject>() {
			@Override
			public void onFailure(final Throwable caught) {
				doFailure(this);
			}

			@Override
			public void onSuccess(final JavaScriptObject jsObject) {
				doSuccess(searchTerm, this, jsObject, loadMore);
			}
		};
		_currentRequests.add(autocompleteReq);
		final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(url, autocompleteReq);
	}

	private void getStringSearchResults(final String searchTerm, final boolean loadMore) {
		final AsyncCallback<CursoredSearchResult> searchReq = new AsyncCallback<CursoredSearchResult>() {

			@Override
			public void onFailure(Throwable caught) {
				doFailure(this);
			}

			@Override
			public void onSuccess(CursoredSearchResult result) {
				doSuccess(searchTerm, this, result, loadMore);
			}
		};

		if (_searchType == SearchType.USERS) {
			_api.call(_api.service().search().searchUsers(searchTerm, _cursor), searchReq);
		}
		else if (_searchType == SearchType.LISTS) {
			_api.call(_api.service().search().searchFaveLists(searchTerm, _cursor), searchReq);
		}

		_currentRequests.add(searchReq);
	}

	private void getSongTypeSuggestions() {
		deselect();
		_currentRequests.clear();
		List<SearchType> suggestions = new ArrayList<>();
		suggestions.add(SearchType.SONGS);
		suggestions.add(SearchType.USERS);
		suggestions.add(SearchType.LISTS);
		_currentSuggestions = suggestions;
		getView().setSongTypeSuggestions(suggestions);
		_maxSelection = suggestions.size() - 1;
	}

	private void doFailure(AsyncCallback<?> request) {
		_currentRequests.remove(request);
	}

	private void doSuccess(String searchTerm, AsyncCallback<?> request, Object result, boolean loadMore) {
		if (!loadMore) {
			_selection = -1;
		}

		// If it's not the latest request, remove it
		if (_currentRequests.indexOf(request) != _currentRequests.size() - 1 || _currentRequests.indexOf(request) == -1) {
			_currentRequests.remove(request);
			return;
		}

		_currentRequests.clear();

		List<?> results = new ArrayList<>();

		if (result instanceof JavaScriptObject) {

			final JSONObject obj = new JSONObject((JavaScriptObject)result);
			SearchResultMapper mapper = GWT.create(SearchResultMapper.class);
			SearchResult searchResult = mapper.read(obj.toString());
			results = searchResult.getResults();

			if (loadMore) {
				((List<SongDto>)_currentSuggestions).addAll((List<SongDto>)results);
			}
			else {
				_currentSuggestions = searchResult.getResults();
			}
			getView().setSongSuggestions((List<SongDto>)results, loadMore);
		}
		else if (result instanceof CursoredSearchResult) {

			List<String> suggestions = new ArrayList<>();
			for (StringResult stringResult : ((CursoredSearchResult)result).getSearchResults().getItems()) {
				suggestions.add(stringResult.getValue());
			}
			results = suggestions;

			if (loadMore) {
				((List<String>)_currentSuggestions).addAll(suggestions);
			}
			else {
				_currentSuggestions = results;
			}
			getView().setStringSuggestions(suggestions, loadMore);

			_cursor = ((CursoredSearchResult)result).getCursor();
		}

		_maxSelection = _currentSuggestions.size() - 1;

		if (_completedAction != null) {
			_completedAction.execute();
		}
	}

	/**
	 * Sets the search to a single SearchType and hides the SearchType selector
	 * 
	 * @param searchType
	 */
	public void setSingleSearch(SearchType searchType) {
		setSearchType(searchType);

		getView().setSingleSearch(true);
		getView().setPlaceHolder("Search " + searchType.toString().toLowerCase());
	}

	public void setMultiSearch() {
		getView().setSingleSearch(false);
	}

	public void setSuggestionSelectedAction(SuggestionSelectedAction action) {
		_selectedAction = action;
	}

	public void setSearchCompletedAction(SearchCompletedAction action) {
		_completedAction = action;
	}

	public void setDarkText(boolean darkText) {
		getView().setDarkText(darkText);
	}

	@Override
	public void clearSearchResults() {
		_page = 0;
		_maxSelection = 0;
		deselect();
		_cursor = null;
	}

	@Override
	public int getSelection() {
		return _selection;
	}

	@Override
	public void setSelection(int position) {
		if (position >= 0 && position <= _maxSelection) {
			_selection = position;
		}
		else if (position < 0) {
			deselect();
		}
	}

	@Override
	public int getMaxSelection() {
		return _maxSelection;
	}

	@Override
	public void deselect() {
		_selection = -1;
	}

	@Override
	public void incrementSelection() {
		setSelection(getSelection() + 1);
	}

	@Override
	public void decrementSelection() {
		setSelection(getSelection() - 1);
	}

	@Override
	public void loadMore() {
		if (_currentSuggestions.size() % SELECTIONS_PER_PAGE == 0) {
			_page++;
			getSearchResults(_lastSearchTerm, true);
		}
	}

	public void setSearchType(SearchType searchType) {
		if (searchType == null) {
			searchType = SearchType.SONGS;
		}

		_searchType = searchType;
		clearSearchResults();
		getView().setSelectedSearchType(searchType);
	}

	public void setFullPageSearch(boolean fullPage) {
		getView().setFullPageSearch(fullPage);
	}

	public void focus() {
		getView().focus();
	}

	@Override
	public void selectSuggestion() {
		if (_searchType == null) {
			setSearchType(((List<SearchType>)_currentSuggestions).get(_selection));
			return;
		}

		if (_selectedAction != null) {
			_selectedAction.execute(_searchType, _currentSuggestions.get(getSelection()));
		}
	}

	@Override
	public int getTotalResults() {
		return _currentSuggestions.size();
	}

}

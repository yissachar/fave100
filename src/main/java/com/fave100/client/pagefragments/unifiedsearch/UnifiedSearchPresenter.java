package com.fave100.client.pagefragments.unifiedsearch;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.entities.SearchResult;
import com.fave100.client.entities.SearchResultMapper;
import com.fave100.client.entities.SongDto;
import com.fave100.client.generated.entities.CursoredSearchResult;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.pagefragments.playlist.PlaylistPresenter;
import com.fave100.client.pagefragments.popups.addsong.AddSongPresenter;
import com.fave100.shared.Constants;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class UnifiedSearchPresenter extends PresenterWidget<UnifiedSearchPresenter.MyView> implements UnifiedSearchUiHandlers {

	public interface MyView extends View, HasUiHandlers<UnifiedSearchUiHandlers> {
		void setSongSuggestions(List<SongDto> songs, boolean loadMore);

		void setStringSuggestions(List<String> suggestions, boolean loadMore);

		void setSongTypeSuggestions(List<SearchType> suggestions);

		void setSelectedSearchType(SearchType searchType);
	}

	public final static int SELECTIONS_PER_PAGE = 5;

	private int _page = 0;
	private int _selection = -1;
	private int _maxSelection = 0;
	private String _lastSearchTerm = "";
	private String _cursor;
	private boolean _addMode;
	private SearchType _searchType = SearchType.SONGS;
	private final List<AsyncCallback<?>> _currentRequests = new ArrayList<>();
	private List<?> _currentSuggestions;
	private PlaceManager _placeManager;
	private RestDispatchAsync _dispatcher;
	private RestServiceFactory _restServiceFactory;
	private PlaylistPresenter _playlistPresenter;
	private CurrentUser _currentUser;
	@Inject AddSongPresenter _addSongPresenter;

	@Inject
	UnifiedSearchPresenter(EventBus eventBus, MyView view, PlaceManager placeManager, RestDispatchAsync dispatcher, RestServiceFactory restServiceFactory,
							PlaylistPresenter playlistPresenter, CurrentUser currentUser) {
		super(eventBus, view);
		_placeManager = placeManager;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
		_playlistPresenter = playlistPresenter;
		_currentUser = currentUser;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	public void getSearchResults(String searchTerm) {
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
			_dispatcher.execute(_restServiceFactory.search().searchUsers(searchTerm, _cursor), searchReq);
		}
		else if (_searchType == SearchType.LISTS) {
			_dispatcher.execute(_restServiceFactory.search().searchFaveLists(searchTerm, _cursor), searchReq);
		}

		_currentRequests.add(searchReq);
	}

	private void getSongTypeSuggestions() {
		_selection = -1;
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
			getView().setStringSuggestions(suggestions, loadMore);
			results = suggestions;

			if (loadMore) {
				((List<String>)_currentSuggestions).addAll(suggestions);
			}
			else {
				_currentSuggestions = results;
			}

			_cursor = ((CursoredSearchResult)result).getCursor();
		}

		_maxSelection = _currentSuggestions.size() - 1;
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
		if (position > _maxSelection) {
			loadMore();
		}
		else if (position >= 0 && position <= _maxSelection) {
			_selection = position;
		}
		else if (position < 0) {
			deselect();
		}
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
		_searchType = searchType;
		getView().setSelectedSearchType(searchType);
	}

	@Override
	public void selectSuggestion() {
		if (_searchType == null) {
			setSearchType(((List<SearchType>)_currentSuggestions).get(_selection));
			return;
		}

		switch (_searchType) {
			case USERS:
				String username = (String)_currentSuggestions.get(getSelection());
				_placeManager.revealPlace(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.USER_PARAM, username)
						.build());
				break;

			case LISTS:
				String list = (String)_currentSuggestions.get(getSelection());
				_placeManager.revealPlace(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.LIST_PARAM, list)
						.build());
				break;

			case SONGS:

				SongDto song = (SongDto)_currentSuggestions.get(getSelection());
				if (_addMode && _currentUser.isLoggedIn()) {
					PlaceRequest currentPlace = _placeManager.getCurrentPlaceRequest();

					if (_currentUser.getHashtags().size() == 1) {
						_currentUser.addSong(song.getId(), Constants.DEFAULT_HASHTAG, song.getSong(), song.getArtist());
					}
					else if (currentPlace.getNameToken().equals(NameTokens.lists)
							&& currentPlace.getParameter(PlaceParams.USER_PARAM, "").equals(_currentUser.getUsername())) {

						_currentUser.addSong(song.getId(), currentPlace.getParameter(PlaceParams.LIST_PARAM, Constants.DEFAULT_HASHTAG), song.getSong(), song.getArtist());
					}
					else {
						_addSongPresenter.setSongToAddId(song.getId());
						_addSongPresenter.setSongToAddName(song.getSong());
						_addSongPresenter.setSongToAddArtist(song.getArtist());
						addToPopupSlot(_addSongPresenter);
					}
				}
				else {
					_playlistPresenter.playSong(song.getId(), song.getSong(), song.getArtist());
				}
				break;

			default:
				break;
		}
	}

	@Override
	public void setAddMode(boolean addMode) {
		_addMode = addMode;
	}

	@Override
	public String getHelpText() {
		StringBuffer sb = new StringBuffer();
		sb.append("You are currently in ");
		sb.append(_addMode ? "add" : "browse");
		sb.append(" mode. Selecting a song will ");
		if (_addMode) {
			PlaceRequest currentPlace = _placeManager.getCurrentPlaceRequest();
			if (_currentUser.getHashtags().size() == 1) {
				sb.append("add it your ");
				sb.append(Constants.DEFAULT_HASHTAG);
				sb.append(" list");
			}
			else if (currentPlace.getNameToken().equals(NameTokens.lists)
					&& currentPlace.getParameter(PlaceParams.USER_PARAM, "").equals(_currentUser.getUsername())) {
				sb.append("add it your ");
				sb.append(currentPlace.getParameter(PlaceParams.LIST_PARAM, Constants.DEFAULT_HASHTAG));
				sb.append(" list");
			}
			else {
				sb.append("prompt you to add it to one of your lists");
			}
			sb.append(".");
		}
		else {
			sb.append("will stop the currently playing song and start playing the selected song.");
		}

		return sb.toString();
	}

	@Override
	public int getTotalResults() {
		return _currentSuggestions.size();
	}
}

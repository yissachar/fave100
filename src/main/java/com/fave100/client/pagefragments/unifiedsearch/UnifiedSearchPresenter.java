package com.fave100.client.pagefragments.unifiedsearch;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.StorageManager;
import com.fave100.client.entities.SearchResult;
import com.fave100.client.entities.SearchResultMapper;
import com.fave100.client.entities.SongDto;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.generated.entities.CursoredSearchResult;
import com.fave100.client.generated.entities.StringResult;
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

		void setAddMode(boolean addMode);

		void addHelpBubble();
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
	private EventBus _eventBus;
	private PlaceManager _placeManager;
	private FaveApi _api;
	private PlaylistPresenter _playlistPresenter;
	private CurrentUser _currentUser;
	private StorageManager _storageManager;
	@Inject AddSongPresenter _addSongPresenter;

	@Inject
	UnifiedSearchPresenter(EventBus eventBus, MyView view, PlaceManager placeManager, FaveApi api, PlaylistPresenter playlistPresenter,
							CurrentUser currentUser, StorageManager storageManager) {
		super(eventBus, view);
		_eventBus = eventBus;
		_placeManager = placeManager;
		_api = api;
		_playlistPresenter = playlistPresenter;
		_currentUser = currentUser;
		_storageManager = storageManager;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		setSearchType(_storageManager.getSearchType());
		setAddMode(_storageManager.isSearchAddMode());

		CurrentUserChangedEvent.register(_eventBus, new CurrentUserChangedEvent.Handler() {

			@Override
			public void onCurrentUserChanged(CurrentUserChangedEvent event) {
				setSearchType(_storageManager.getSearchType());
				setAddMode(_storageManager.isSearchAddMode());
			}
		});

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
	}

	public void showAddSongsHelpBubble() {
		getView().addHelpBubble();
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
		_storageManager.setSearchType(searchType);
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
		getView().setAddMode(addMode);
		_storageManager.setSearchAddMode(addMode);
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

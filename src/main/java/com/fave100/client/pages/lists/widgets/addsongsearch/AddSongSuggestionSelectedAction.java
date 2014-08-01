package com.fave100.client.pages.lists.widgets.addsongsearch;

import com.fave100.client.CurrentUser;
import com.fave100.client.entities.SongDto;
import com.fave100.client.widgets.search.SearchType;
import com.fave100.client.widgets.search.SuggestionSelectedAction;
import com.fave100.shared.Constants;
import com.fave100.shared.place.PlaceParams;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class AddSongSuggestionSelectedAction implements SuggestionSelectedAction {

	private PlaceManager _placeManager;
	private CurrentUser _currentUser;
	private PopupView _addSongSearchView;

	public AddSongSuggestionSelectedAction(PlaceManager placeManager, CurrentUser currentUser, PopupView addSongSearchView) {
		_placeManager = placeManager;
		_currentUser = currentUser;
		_addSongSearchView = addSongSearchView;
	}

	@Override
	public void execute(SearchType searchType, Object selectedItem) {
		SongDto song = (SongDto)selectedItem;

		PlaceRequest currentPlace = _placeManager.getCurrentPlaceRequest();
		String listName = currentPlace.getParameter(PlaceParams.LIST_PARAM, Constants.DEFAULT_HASHTAG);
		_currentUser.addSong(song.getId(), listName, song.getSong(), song.getArtist());

		_addSongSearchView.hide();
	}

}

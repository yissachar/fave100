package com.fave100.client.pages.lists;

import com.fave100.client.entities.SongDto;
import com.fave100.client.pagefragments.playlist.PlaylistPresenter;
import com.fave100.client.widgets.search.SearchType;
import com.fave100.client.widgets.search.SuggestionSelectedAction;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class UnifiedSearchSuggestionSelectedAction implements SuggestionSelectedAction {

	private PlaceManager _placeManager;
	private PlaylistPresenter _playlistPresenter;

	public UnifiedSearchSuggestionSelectedAction(PlaceManager placeManager, PlaylistPresenter playlistPresenter) {
		_placeManager = placeManager;
		_playlistPresenter = playlistPresenter;
	}

	public void execute(SearchType searchType, Object selectedItem) {
		switch (searchType) {
			case USERS:
				String username = (String)selectedItem;
				_placeManager.revealPlace(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.USER_PARAM, username)
						.build());
				break;

			case LISTS:
				String list = (String)selectedItem;
				_placeManager.revealPlace(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.LIST_PARAM, list)
						.build());
				break;

			case SONGS:
				SongDto song = (SongDto)selectedItem;
				_playlistPresenter.playSong(song.getId(), song.getSong(), song.getArtist());
				break;

			default:
				break;
		}
	}
}

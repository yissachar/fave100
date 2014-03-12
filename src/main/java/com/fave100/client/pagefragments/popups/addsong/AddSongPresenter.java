package com.fave100.client.pagefragments.popups.addsong;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.favelist.AddSongListsSelectedEvent;
import com.fave100.client.generated.entities.FaveItem;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

public class AddSongPresenter extends PresenterWidget<AddSongPresenter.MyView> implements AddSongUiHandlers {
	public interface MyView extends PopupView, HasUiHandlers<AddSongUiHandlers> {
		void setListNames(List<String> listNames, String songName);
	}

	private EventBus _eventBus;
	private CurrentUser _currentUser;
	private String _songToAddId;
	private String _songToAddName;
	private String _songToAddArtist;

	@Inject
	AddSongPresenter(final EventBus eventBus, final MyView view, final CurrentUser currentUser) {
		super(eventBus, view);
		_eventBus = eventBus;
		_currentUser = currentUser;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {
		super.onReveal();

		List<String> listNames = new ArrayList<String>(_currentUser.getHashtags());
		// Remove lists that the song is already in (won't always work, since we don't retrieve all users lists)
		for (Map.Entry<String, List<FaveItem>> entry : _currentUser.getFaveLists().entrySet()) {
			boolean inList = false;
			for (FaveItem faveItem : entry.getValue()) {
				if (faveItem.getId().equals(getSongToAddId())) {
					inList = true;
				}
			}

			if (inList)
				listNames.remove(entry.getKey());
		}
		getView().setListNames(listNames, getSongToAddName());
	}

	@Override
	public void listsSelected(List<String> selectedLists) {
		_eventBus.fireEvent(new AddSongListsSelectedEvent(selectedLists, getSongToAddId(), getSongToAddName(), getSongToAddArtist()));
	}

	/* Getters and Setters */

	public String getSongToAddId() {
		return _songToAddId;
	}

	public void setSongToAddId(String songToAddId) {
		_songToAddId = songToAddId;
	}

	public String getSongToAddName() {
		return _songToAddName;
	}

	public void setSongToAddName(String songToAddName) {
		_songToAddName = songToAddName;
	}

	public String getSongToAddArtist() {
		return _songToAddArtist;
	}

	public void setSongToAddArtist(String songToAddArtist) {
		_songToAddArtist = songToAddArtist;
	}
}

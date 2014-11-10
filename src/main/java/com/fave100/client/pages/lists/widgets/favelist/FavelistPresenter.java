package com.fave100.client.pages.lists.widgets.favelist;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.Notification;
import com.fave100.client.events.LoginDialogRequestedEvent;
import com.fave100.client.events.favelist.FaveItemAddedEvent;
import com.fave100.client.events.favelist.FaveListSizeChangedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.FaveItem;
import com.fave100.client.generated.entities.FaveItemCollection;
import com.fave100.client.generated.entities.WhylineEdit;
import com.fave100.client.pagefragments.playlist.PlaylistPresenter;
import com.fave100.client.pagefragments.popups.addsong.AddSongPresenter;
import com.fave100.client.pages.lists.widgets.favelist.widgets.AddSongAfterLoginAction;
import com.fave100.client.pages.lists.widgets.favelist.widgets.FavePickWidget;
import com.fave100.shared.Constants;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.shared.RestCallback;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

public class FavelistPresenter extends
		PresenterWidget<FavelistPresenter.MyView>
		implements FavelistUiHandlers {

	public interface MyView extends View, HasUiHandlers<FavelistUiHandlers> {
		void setList(List<FavePickWidget> pickWidgets);

		void addPick(FavePickWidget widget);

		void swapPicks(int indexA, int indexB);

		void hideNoItemsMessage();

		void setListFound(boolean found);

		void clearState();
	}

	private EventBus _eventBus;
	private FaveApi _api;
	// The user whose favelist we are showing
	private AppUser _user;
	// The currently logged in user
	private CurrentUser _currentUser;
	private PlaceManager _placeManager;
	private String _listMode;
	private PlaylistPresenter _playlistPresenter;
	private String _hashtag;
	private List<FavePickWidget> _widgets;
	private boolean _descending;
	@Inject private AddSongPresenter _addSongPresenter;

	@Inject
	public FavelistPresenter(final EventBus eventBus, final MyView view, final FaveApi api, final PlaceManager placeManager, final CurrentUser currentUser,
								PlaylistPresenter playlistPresenter) {
		super(eventBus, view);
		_eventBus = eventBus;
		_api = api;
		_currentUser = currentUser;
		_placeManager = placeManager;
		_playlistPresenter = playlistPresenter;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();

		// Update FaveList when it changes
		FaveItemAddedEvent.register(_eventBus, new FaveItemAddedEvent.Handler() {
			@Override
			public void onFaveItemAdded(final FaveItemAddedEvent event) {
				if (!isEditable())
					return;

				final FaveItem item = event.getFaveItemDto();
				final FavePickWidget widget = new FavePickWidget(_eventBus, item, _widgets.size() + 1, isEditable(), false, _widgets.size() + 1, FavelistPresenter.this);
				getView().addPick(widget);
				_widgets.add(widget);

				final int listSize = _currentUser.getFaveLists().get(_currentUser.getCurrentHashtag()).size();
				if (listSize == 1) {
					// Only one song in list, focus whyline for convenience
					widget.focusWhyline();
					// Show help bubble if on default list
					if (_currentUser.getCurrentHashtag().equals(Constants.DEFAULT_HASHTAG)) {
						widget.showWhylineHelpBubble();
					}
				}
				else if (listSize > 1) {
					// Focus rank for easy rank changing
					widget.focusRank();
					// Show help bubble if on default list
					if (_currentUser.getCurrentHashtag().equals(Constants.DEFAULT_HASHTAG) && listSize == 2) {
						widget.showRankWhylineHelpBubble();
					}
				}
			}
		});
	}

	public void clearFavelist() {
		getView().clearState();
	}

	public void refreshFavelist() {
		getView().clearState();

		if (_hashtag == null) {
			_hashtag = Constants.DEFAULT_HASHTAG;
		}

		final String hashtagPerRequest = _hashtag;

		// Get the FaveList locally if possible 
		if (_currentUser.isViewingOwnList() && _currentUser.getFaveList() != null) {
			buildWidgets(_currentUser.getFaveList());
			return;
		}
		// Otherwise get it from the server if we are requesting a user's list
		else if (_user != null) {
			_api.call(_api.service().users().getFaveList(_user.getUsername(), _hashtag), new AsyncCallback<FaveItemCollection>() {

				@Override
				public void onFailure(Throwable caught) {
					getView().setListFound(false);
				}

				@Override
				public void onSuccess(FaveItemCollection result) {
					// Make sure user still not null when results fetched, and results for hashtag is same hashtag as latest requested hashtag, otherwise could be stale data
					if (_user != null && hashtagPerRequest.equals(_hashtag)) {
						if (_currentUser.isViewingOwnList()) {
							_currentUser.setFaveList(result.getItems());
						}
						buildWidgets(result.getItems());
					}
				}
			});

		}
		// No user, get the global list 
		else {

			_api.call(_api.service().favelists().getMasterFaveList(_hashtag, _listMode), new AsyncCallback<FaveItemCollection>() {

				@Override
				public void onFailure(Throwable caught) {
					getView().setListFound(false);
				}

				@Override
				public void onSuccess(FaveItemCollection result) {
					// Make sure user still null when results fetched, and results for hashtag is same hashtag as latest requested hashtag, otherwise could be stale data
					if (_user == null && hashtagPerRequest.equals(_hashtag))
						buildWidgets(result.getItems());
				}
			});
		}
	}

	private void buildWidgets(final List<FaveItem> faveList) {
		_descending = false;
		buildWidgets(faveList, false);
	}

	private void buildWidgets(final List<FaveItem> faveList, boolean descending) {
		final List<FavePickWidget> pickWidgets = new ArrayList<FavePickWidget>();
		int i = 1;
		if (descending) {
			i = faveList.size();
		}

		for (final FaveItem item : faveList) {
			final FavePickWidget widget = new FavePickWidget(_eventBus, item, i, isEditable(), isGlobalList(), faveList.size(), this);
			pickWidgets.add(widget);
			if (descending) {
				i--;
			}
			else {
				i++;
			}
		}
		_widgets = pickWidgets;

		getView().setList(pickWidgets);
		Window.scrollTo(0, 0);
		_eventBus.fireEvent(new FaveListSizeChangedEvent(faveList.size()));
	}

	@Override
	public void addSong(final String songId, final String song, final String artist, boolean forceAddToCurrentList) {
		if (!_currentUser.isLoggedIn()) {
			_currentUser.setAfterLoginAction(new AddSongAfterLoginAction(this, songId, song, artist));
			_eventBus.fireEvent(new LoginDialogRequestedEvent());
		}
		else {
			if (_currentUser.getHashtags().size() == 1 || forceAddToCurrentList) {
				_currentUser.addSong(songId, song, artist);
			}
			else {
				_addSongPresenter.setSongToAddId(songId);
				_addSongPresenter.setSongToAddName(song);
				_addSongPresenter.setSongToAddArtist(artist);
				addToPopupSlot(_addSongPresenter);
			}
		}
	}

	@Override
	public void removeSong(final String songId, final int index) {
		// Only bother with updating list if we are on the user's page 
		if (!isEditable()) {
			return;
		}

		// Re-rank on client
		for (int i = index + 1; i < _widgets.size(); i++) {
			_widgets.get(i).setRank(i);
		}
		_widgets.remove(index);
		_currentUser.getFaveList().remove(index);
		// Send request for server to remove it
		_api.call(_api.service().user().removeFaveItemForCurrentUser(_hashtag, songId), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(Void result) {
				// Do nothing, since we have already removed the song on the client before sending the server request
			}
		});
	}

	@Override
	public void editWhyline(final String songId, final String whyline) {
		WhylineEdit whylineEdit = new WhylineEdit();
		whylineEdit.setListName(_hashtag);
		whylineEdit.setSongId(songId);
		whylineEdit.setWhyline(whyline);

		_api.call(_api.service().user().editWhylineForCurrentUser(whylineEdit), new RestCallback<Void>() {

			@Override
			public void setResponse(Response response) {
				if (response.getStatusCode() >= 400) {
					Notification.show(response.getText());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// Already handled in setResponse
			}

			@Override
			public void onSuccess(Void result) {
				// Set client to match
				for (int i = 0; i < _currentUser.getFaveList().size(); i++) {
					final FaveItem faveItem = _currentUser.getFaveList().get(i);
					if (faveItem.getSongID().equals(songId)) {
						FaveItem newfaveItem = new FaveItem();
						newfaveItem.setSongID(songId);
						newfaveItem.setWhyline(whyline);
						newfaveItem.setArtist(faveItem.getArtist());
						newfaveItem.setSong(faveItem.getSong());

						_currentUser.getFaveList().add(i, newfaveItem);
						_currentUser.getFaveList().remove(faveItem);
					}
				}
			}
		});
	}

	@Override
	public void changeSongPosition(final String songId, final int currentIndex, final int newIndex) {
		// Only bother with updating list if we are on the user's page 
		if (!isEditable()) {
			return;
		}

		// If it is the same position, ignore
		if (currentIndex == newIndex)
			return;

		// If index out of range, refresh with correct values
		if (newIndex < 0 || newIndex >= _currentUser.getFaveList().size()) {
			_widgets.get(currentIndex).setRank(currentIndex + 1);
			return;
		}

		// Save on server
		_api.call(_api.service().user().rerankFaveItemForCurrentUser(_hashtag, songId, newIndex), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Void result) {
				// If successfully saved on server, manually set client to match
				final FaveItem toRerank = _currentUser.getFaveList().get(currentIndex);
				_currentUser.getFaveList().remove(toRerank);
				_currentUser.getFaveList().add(newIndex, toRerank);

				// And then manually update the widget view to match
				FavePickWidget pickToRank = _widgets.get(currentIndex);
				_widgets.remove(pickToRank);
				_widgets.add(newIndex, pickToRank);
				int i = 1;
				for (final FavePickWidget widget : _widgets) {
					widget.setRank(i);
					i++;
				}
				getView().swapPicks(currentIndex, newIndex);
				// Because of complications from the floating search, it is better to try to scroll the previous widget
				if (newIndex < currentIndex && newIndex - 1 >= 0)
					pickToRank = _widgets.get(newIndex - 1);
				$(pickToRank).scrollIntoView(true);
			}
		});
	}

	@Override
	public void playSong(String songId) {
		List<FaveItem> faveItems = new ArrayList<>();
		for (FavePickWidget widget : _widgets) {
			faveItems.add(widget.getFaveItem());
		}

		_playlistPresenter.playSong(songId, _hashtag, _user != null ? _user.getUsername() : "", isGlobalList(), faveItems);
	}

	public void switchDirection() {
		List<FaveItem> faveItems = new ArrayList<FaveItem>();
		for (int i = _widgets.size() - 1; i >= 0; i--) {
			FavePickWidget widget = _widgets.get(i);
			faveItems.add(widget.getFaveItem());
		}
		_descending = !_descending;
		buildWidgets(faveItems, _descending);
	}

	public void resetDirection() {
		if (_descending) {
			switchDirection();
		}
	}

	private boolean isEditable() {
		return _currentUser.isLoggedIn() && _currentUser.equals(_user);
	}

	private boolean isGlobalList() {
		return _user == null;
	}

	/* Getters and Setters */

	public AppUser getUser() {
		return _user;
	}

	public void setUser(final AppUser user) {
		_user = user;
	}

	public String getHashtag() {
		return _hashtag;
	}

	public void setHashtag(final String hashtag) {
		_hashtag = hashtag;
	}

	public void setListMode(String listMode) {
		_listMode = listMode;
	}

}
package com.fave100.client.pages.lists.widgets.favelist;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.Notification;
import com.fave100.client.events.favelist.FaveItemAddedEvent;
import com.fave100.client.events.favelist.FaveListSizeChangedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.FaveItem;
import com.fave100.client.generated.entities.FaveItemCollection;
import com.fave100.client.generated.entities.WhylineEdit;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.pagefragments.popups.addsong.AddSongPresenter;
import com.fave100.client.pages.lists.widgets.favelist.widgets.FavePickWidget;
import com.fave100.shared.Constants;
import com.fave100.shared.place.NameTokens;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.dispatch.rest.shared.RestCallback;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class FavelistPresenter extends
		PresenterWidget<FavelistPresenter.MyView>
		implements FavelistUiHandlers {

	public interface MyView extends View, HasUiHandlers<FavelistUiHandlers> {
		void setList(List<FavePickWidget> pickWidgets);

		void addPick(FavePickWidget widget);

		void swapPicks(int indexA, int indexB);

		void hideNoItemsMessage();

		void setListFound(boolean found);
	}

	public interface WhyLineChanged {
		void onChange(String songID, String whyLine);
	}

	public interface RankChanged {
		void onChange(String songID, int currentIndex, int newIndex);
	}

	public interface ItemDeleted {
		void onDeleted(String songID, int index);
	}

	public interface ItemAdded {
		void onAdded(String songID, String song, String artist);
	}

	private EventBus eventBus;
	private RestDispatchAsync _dispatcher;
	private RestServiceFactory _restServiceFactory;
	// The user whose favelist we are showing
	private AppUser user;
	// The currently logged in user
	private CurrentUser currentUser;
	private PlaceManager _placeManager;
	// The list to work with
	private String hashtag;
	private List<FavePickWidget> widgets;
	@Inject private AddSongPresenter addSongPresenter;

	private WhyLineChanged _whyLineChanged = new WhyLineChanged() {
		@Override
		public void onChange(final String songID, final String whyLine) {
			editWhyline(songID, whyLine);
		}
	};

	private RankChanged _rankChanged = new RankChanged() {
		@Override
		public void onChange(final String songID, final int currentIndex, final int newIndex) {
			changeSongPosition(songID, currentIndex, newIndex);
		}
	};

	private ItemDeleted _itemDeleted = new ItemDeleted() {
		@Override
		public void onDeleted(final String songID, final int index) {
			removeSong(songID, index);
		}
	};

	private ItemAdded _itemAdded = new ItemAdded() {
		@Override
		public void onAdded(final String songID, final String song, final String artist) {
			addSong(songID, song, artist, false);
		}
	};

	@Inject
	public FavelistPresenter(final EventBus eventBus, final MyView view, RestDispatchAsync dispatcher, RestServiceFactory restServiceFactory,
								final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view);
		this.eventBus = eventBus;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
		this.currentUser = currentUser;
		_placeManager = placeManager;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();

		// Update FaveList when it changes
		FaveItemAddedEvent.register(eventBus, new FaveItemAddedEvent.Handler() {
			@Override
			public void onFaveItemAdded(final FaveItemAddedEvent event) {
				if (isEditable()) {
					final FaveItem item = event.getFaveItemDto();
					final FavePickWidget widget = new FavePickWidget(eventBus, item, widgets.size() + 1, isEditable(), _whyLineChanged, _rankChanged, _itemDeleted, _itemAdded, user.getUsername(), hashtag);
					getView().addPick(widget);
					widgets.add(widget);

					final int listSize = currentUser.getFaveLists().get(currentUser.getCurrentHashtag()).size();
					if (listSize == 1) {
						// Only one song in list, focus whyline for convenience
						widget.focusWhyline();
						// Show help bubble if on default list
						if (currentUser.getCurrentHashtag().equals(Constants.DEFAULT_HASHTAG))
							widget.showWhylineHelpBubble();
					}
					else if (listSize > 1) {
						// Focus rank for easy rank changing
						widget.focusRank();
						// Show help bubble if on default list
						if (currentUser.getCurrentHashtag().equals(Constants.DEFAULT_HASHTAG) && listSize == 2) {
							widget.showRankWhylineHelpBubble();
						}
					}
				}
			}
		});
	}

	public void clearFavelist() {
		getView().setList(null);
	}

	public void refreshFavelist(final boolean ownList) {
		getView().setList(null);
		getView().hideNoItemsMessage();

		if (hashtag == null) {
			hashtag = Constants.DEFAULT_HASHTAG;
		}

		final String hashtagPerRequest = hashtag;

		// Get the FaveList locally if possible 
		if (ownList && currentUser.getFaveList() != null) {
			buildWidgets(currentUser.getFaveList());
			return;
		}
		// Otherwise get it from the server if we are requesting a user's list
		else if (user != null) {
			_dispatcher.execute(_restServiceFactory.users().getFaveList(user.getUsername(), hashtag), new AsyncCallback<FaveItemCollection>() {

				@Override
				public void onFailure(Throwable caught) {
					getView().setListFound(false);
				}

				@Override
				public void onSuccess(FaveItemCollection result) {
					// Make sure user still not null when results fetched, and results for hashtag is same hashtag as latest requested hashtag, otherwise could be stale data
					if (user != null && hashtagPerRequest.equals(hashtag)) {
						if (ownList)
							currentUser.setFaveList(result.getItems());
						buildWidgets(result.getItems());
					}
				}
			});

		}
		// No user, get the global list 
		else {

			_dispatcher.execute(_restServiceFactory.favelists().getMasterFaveList(hashtag), new AsyncCallback<FaveItemCollection>() {

				@Override
				public void onFailure(Throwable caught) {
					getView().setListFound(false);
				}

				@Override
				public void onSuccess(FaveItemCollection result) {
					// Make sure user still null when results fetched, and results for hashtag is same hashtag as latest requested hashtag, otherwise could be stale data
					if (user == null && hashtagPerRequest.equals(hashtag))
						buildWidgets(result.getItems());
				}
			});
		}
	}

	private void buildWidgets(final List<FaveItem> faveList) {
		final List<FavePickWidget> pickWidgets = new ArrayList<FavePickWidget>();
		int i = 1;
		for (final FaveItem item : faveList) {
			final String username = user != null ? user.getUsername() : "";
			final FavePickWidget widget = new FavePickWidget(eventBus, item, i, isEditable(), _whyLineChanged, _rankChanged, _itemDeleted, _itemAdded, username, hashtag);
			pickWidgets.add(widget);
			i++;
		}
		widgets = pickWidgets;

		getView().setList(pickWidgets);
		Window.scrollTo(0, 0);
		eventBus.fireEvent(new FaveListSizeChangedEvent(faveList.size()));
	}

	@Override
	public void addSong(final String songID, final String song, final String artist, boolean forceAddToCurrentList) {
		if (!currentUser.isLoggedIn()) {
			_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
		}
		else {
			if (currentUser.getHashtags().size() == 1 || forceAddToCurrentList) {
				currentUser.addSong(songID, song, artist);
			}
			else {
				addSongPresenter.setSongToAddId(songID);
				addSongPresenter.setSongToAddName(song);
				addSongPresenter.setSongToAddArtist(artist);
				addToPopupSlot(addSongPresenter);
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
		for (int i = index + 1; i < widgets.size(); i++) {
			widgets.get(i).setRank(i);
		}
		widgets.remove(index);
		currentUser.getFaveList().remove(index);
		// Send request for server to remove it
		_dispatcher.execute(_restServiceFactory.user().removeFaveItemForCurrentUser(hashtag, songId), new AsyncCallback<Void>() {

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
		whylineEdit.setListName(hashtag);
		whylineEdit.setSongId(songId);
		whylineEdit.setWhyline(whyline);

		_dispatcher.execute(_restServiceFactory.user().editWhylineForCurrentUser(whylineEdit), new RestCallback<Void>() {

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
				for (int i = 0; i < currentUser.getFaveList().size(); i++) {
					final FaveItem faveItem = currentUser.getFaveList().get(i);
					if (faveItem.getSongID().equals(songId)) {
						FaveItem newfaveItem = new FaveItem();
						newfaveItem.setSongID(songId);
						newfaveItem.setId(songId);
						newfaveItem.setWhyline(whyline);
						newfaveItem.setArtist(faveItem.getArtist());
						newfaveItem.setSong(faveItem.getSong());

						currentUser.getFaveList().add(i, newfaveItem);
						currentUser.getFaveList().remove(faveItem);
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
		if (newIndex < 0 || newIndex >= currentUser.getFaveList().size()) {
			widgets.get(currentIndex).setRank(currentIndex + 1);
			return;
		}

		// Save on server
		_dispatcher.execute(_restServiceFactory.user().rerankFaveItemForCurrentUser(hashtag, songId, newIndex), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Void result) {
				// If successfully saved on server, manually set client to match
				final FaveItem toRerank = currentUser.getFaveList().get(currentIndex);
				currentUser.getFaveList().remove(toRerank);
				currentUser.getFaveList().add(newIndex, toRerank);

				// And then manually update the widget view to match
				FavePickWidget pickToRank = widgets.get(currentIndex);
				widgets.remove(pickToRank);
				widgets.add(newIndex, pickToRank);
				int i = 1;
				for (final FavePickWidget widget : widgets) {
					widget.setRank(i);
					i++;
				}
				getView().swapPicks(currentIndex, newIndex);
				// Because of complications from the floating search, it is better to try to scroll the previous widget
				if (newIndex < currentIndex && newIndex - 1 >= 0)
					pickToRank = widgets.get(newIndex - 1);
				$(pickToRank).scrollIntoView(true);
			}
		});
	}

	private boolean isEditable() {
		return currentUser.isLoggedIn() && currentUser.equals(user);
	}

	/* Getters and Setters */

	public AppUser getUser() {
		return user;
	}

	public void setUser(final AppUser user) {
		this.user = user;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(final String hashtag) {
		this.hashtag = hashtag;
	}

}
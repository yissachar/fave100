package com.fave100.client.pagefragments.favelist;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.Notification;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.events.FaveListSizeChangedEvent;
import com.fave100.client.pagefragments.favelist.widgets.FavePickWidget;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.exceptions.favelist.BadWhylineException;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.fave100.shared.requestfactory.FaveListRequest;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class FavelistPresenter extends
		PresenterWidget<FavelistPresenter.MyView>
		implements FavelistUiHandlers {

	public interface MyView extends View, HasUiHandlers<FavelistUiHandlers> {
		void setList(List<FavePickWidget> pickWidgets);

		void addPick(FavePickWidget widget);

		void swapPicks(int indexA, int indexB);
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
	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;
	// The user whose favelist we are showing
	private AppUserProxy user;
	// The currently logged in user
	private CurrentUser currentUser;
	private List<FaveItemProxy> favelist;
	private List<FavePickWidget> widgets;

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
			addSong(songID, song, artist);
		}
	};

	@Inject
	public FavelistPresenter(final EventBus eventBus, final MyView view,
								final ApplicationRequestFactory requestFactory,
								final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view);
		this.eventBus = eventBus;
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
		this.currentUser = currentUser;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	public void clearFavelist() {
		getView().setList(null);
	}

	public void refreshFavelist(final boolean ownList) {
		getView().setList(null);

		// Get the FaveList locally if possible 
		if (ownList && currentUser.getFaveList() != null) {
			setFavelist(currentUser.getFaveList());
			buildWidgets(favelist);
			return;
		}

		// Otherwise get it from the server
		final Request<List<FaveItemProxy>> req = requestFactory.faveListRequest().getFaveList(user.getUsername(), Constants.DEFAULT_HASHTAG);
		req.fire(new Receiver<List<FaveItemProxy>>() {

			@Override
			public void onSuccess(final List<FaveItemProxy> results) {
				setFavelist(results);
				if (ownList)
					currentUser.setFaveList(favelist);
				buildWidgets(favelist);
			}
		});
	}

	private void buildWidgets(final List<FaveItemProxy> faveList) {
		final List<FavePickWidget> pickWidgets = new ArrayList<FavePickWidget>();
		int i = 1;
		for (final FaveItemProxy item : faveList) {
			final FavePickWidget widget = new FavePickWidget(item, i, isEditable(), _whyLineChanged, _rankChanged, _itemDeleted, _itemAdded, user.getUsername());
			pickWidgets.add(widget);
			i++;
		}
		widgets = pickWidgets;

		getView().setList(pickWidgets);
		eventBus.fireEvent(new FaveListSizeChangedEvent(getFavelist().size()));
	}

	@Override
	public void addSong(final String songID, final String song, final String artist) {

		final FaveListRequest faveListRequest = requestFactory.faveListRequest();

		// Add the song as a FaveItem
		final Request<Void> addReq = faveListRequest.addFaveItemForCurrentUser(Constants.DEFAULT_HASHTAG,
				songID);

		addReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				Notification.show("Song added");
				// Only bother with updating list if we are on the user's page 
				if (isEditable()) {
					// Pretty meh to do it this way, but quickest way for now
					final FaveItemProxy item = new FaveItemProxy() {

						@Override
						public String getWhyline() {
							return null;
						}

						@Override
						public String getSongID() {
							return songID;
						}

						@Override
						public String getSong() {
							return song;
						}

						@Override
						public String getArtist() {
							return artist;
						}
					};
					final FavePickWidget widget = new FavePickWidget(item, widgets.size() + 1, isEditable(), _whyLineChanged, _rankChanged, _itemDeleted, _itemAdded, user.getUsername());
					getView().addPick(widget);
					widgets.add(widget);
					if (getView().asWidget().getElement().getClientHeight() + widget.getElement().getClientHeight() > Window.getClientHeight())
						$(widget).scrollIntoView();
					favelist.add(item);
					if (favelist.size() == 1) {
						// Only one song in list, show help bubble for whyline and focus
						widget.focusWhyline();
						widget.showWhylineHelpBubble();
					}
					else {
						if (favelist.size() == 2) {
							widget.showRankWhylineHelpBubble();
						}
						widget.focusRank();
					}
				}
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				if (failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					eventBus.fireEvent(new CurrentUserChangedEvent(null));
					placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
				}
				else if (failure.getExceptionType().equals(SongLimitReachedException.class.getName())) {
					Notification.show("You cannot have more than 100 songs in list");
				}
				else if (failure.getExceptionType().equals(SongAlreadyInListException.class.getName())) {
					Notification.show("The song is already in your list");
				}
				else {
					// Catch-all
					Notification.show("Error: Could not add song");
				}
			}
		});
	}

	@Override
	public void removeSong(final String songID, final int index) {
		// Only bother with updating list if we are on the user's page 
		if (!isEditable()) {
			return;
		}

		// Re-rank on client
		for (int i = index + 1; i < widgets.size(); i++) {
			widgets.get(i).setRank(i);
		}
		widgets.remove(index);
		favelist.remove(index);
		// Send request for server to remove it
		final Request<Void> req = requestFactory.faveListRequest()
				.removeFaveItemForCurrentUser(Constants.DEFAULT_HASHTAG, songID);
		req.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				// Don't care about success, already did what we need to
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				if (failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					eventBus.fireEvent(new CurrentUserChangedEvent(null));
				}
			}
		});
	}

	@Override
	public void editWhyline(final String songID, final String whyline) {
		final Request<Void> editWhyline = requestFactory.faveListRequest()
				.editWhylineForCurrentUser(Constants.DEFAULT_HASHTAG, songID, whyline);
		editWhyline.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void result) {
				// Set client to match
				for (int i = 0; i < favelist.size(); i++) {
					final FaveItemProxy faveItem = favelist.get(i);
					if (faveItem.getSongID().equals(songID)) {
						final FaveItemProxy newfaveItem = new FaveItemProxy() {
							@Override
							public String getWhyline() {
								return whyline;
							}

							@Override
							public String getSongID() {
								return songID;
							}

							@Override
							public String getSong() {
								return faveItem.getSong();
							}

							@Override
							public String getArtist() {
								return faveItem.getArtist();
							}
						};
						favelist.add(i, newfaveItem);
						favelist.remove(faveItem);
					}
				}
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				if (failure.getExceptionType().equals(BadWhylineException.class.getName())) {
					Notification.show(failure.getMessage());
				}
				if (failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					eventBus.fireEvent(new CurrentUserChangedEvent(null));
				}
			}
		});
	}

	@Override
	public void changeSongPosition(final String songID, final int currentIndex, final int newIndex) {
		// Only bother with updating list if we are on the user's page 
		if (!isEditable()) {
			return;
		}

		// If it is the same position, ignore
		if (currentIndex == newIndex)
			return;

		// If index out of range, refresh with correct values
		if (newIndex < 0 || newIndex >= getFavelist().size()) {
			widgets.get(currentIndex).setRank(currentIndex + 1);
			return;
		}

		// Save on server
		final Request<Void> changePosition = requestFactory.faveListRequest()
				.rerankFaveItemForCurrentUser(Constants.DEFAULT_HASHTAG, songID, newIndex);
		changePosition.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				// If successfully saved on server, manually set client to match
				final FaveItemProxy toRerank = favelist.get(currentIndex);
				favelist.remove(toRerank);
				favelist.add(newIndex, toRerank);

				// And then manually update the widget view to match
				final FavePickWidget pickToRank = widgets.get(currentIndex);
				widgets.remove(pickToRank);
				widgets.add(newIndex, pickToRank);
				int i = 1;
				for (final FavePickWidget widget : widgets) {
					widget.setRank(i);
					i++;
				}
				getView().swapPicks(currentIndex, newIndex);
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				if (failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					eventBus.fireEvent(new CurrentUserChangedEvent(null));
				}
			}
		});
	}

	private boolean isEditable() {
		return currentUser.isLoggedIn() && currentUser.equals(user);
	}

	/* Getters and Setters */

	public AppUserProxy getUser() {
		return user;
	}

	public void setUser(final AppUserProxy user) {
		this.user = user;
	}

	public List<FaveItemProxy> getFavelist() {
		return favelist;
	}

	public void setFavelist(final List<FaveItemProxy> favelist) {
		this.favelist = favelist;
	}

}
package com.fave100.client.pagefragments.favelist;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.Notification;
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
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
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
	}

	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;
	// The user whose favelist we are showing
	private AppUserProxy user;
	// The currently logged in user
	private CurrentUser currentUser;
	private List<FaveItemProxy> favelist;
	private List<FavePickWidget> widgets;

	@Inject
	public FavelistPresenter(final EventBus eventBus, final MyView view,
								final ApplicationRequestFactory requestFactory,
								final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view);
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

	public interface WhyLineChanged {
		void onChange(String songID, String whyLine);
	}

	public interface RankChanged {
		void onChange(String songID, int currentIndex, int newIndex);
	}

	public interface ItemDeleted {
		void onDeleted(String songID);
	}

	public void refreshFavelist() {
		final Request<List<FaveItemProxy>> req = requestFactory.faveListRequest().getFaveList(user.getUsername(), Constants.DEFAULT_HASHTAG);
		req.fire(new Receiver<List<FaveItemProxy>>() {
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
				public void onDeleted(final String songID) {
					removeSong(songID);
				}
			};

			@Override
			public void onSuccess(final List<FaveItemProxy> results) {
				setFavelist(results);
				final boolean editable = (currentUser.isLoggedIn() && currentUser.equals(user));

				final List<FavePickWidget> pickWidgets = new ArrayList<FavePickWidget>();
				int i = 1;
				for (final FaveItemProxy item : results) {
					final FavePickWidget widget = new FavePickWidget(item, i, editable, _whyLineChanged, _rankChanged, _itemDeleted);
					pickWidgets.add(widget);
					i++;
				}
				widgets = pickWidgets;

				getView().setList(pickWidgets);

			}
		});
	}

	@Override
	public void addSong(final String songID) {

		final FaveListRequest faveListRequest = requestFactory.faveListRequest();

		// Add the song as a FaveItem
		final Request<Void> addReq = faveListRequest.addFaveItemForCurrentUser(Constants.DEFAULT_HASHTAG,
				songID);

		addReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				Notification.show("Song added");
				refreshFavelist();
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				if (failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					placeManager.revealPlace(new PlaceRequest(NameTokens.login));
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
	public void removeSong(final String songID) {
		// Send request for server to remove it
		final Request<Void> req = requestFactory.faveListRequest()
				.removeFaveItemForCurrentUser(Constants.DEFAULT_HASHTAG, songID);
		req.fire();
	}

	@Override
	public void editWhyline(final String songID, final String whyline) {
		final Request<Void> editWhyline = requestFactory.faveListRequest()
				.editWhylineForCurrentUser(Constants.DEFAULT_HASHTAG, songID, whyline);
		editWhyline.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void result) {
				// Do nothing
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				if (failure.getExceptionType().equals(BadWhylineException.class.getName())) {
					Notification.show(failure.getMessage());
				}
			}
		});
	}

	@Override
	public void changeSongPosition(final String songID, final int currentIndex, final int newIndex) {
		// If it is the same position, ignore
		if (currentIndex == newIndex)
			return;

		// If index out of range, refresh list with correct values
		if (newIndex < 0 || newIndex >= getFavelist().size()) {
			widgets.get(currentIndex).setRank(currentIndex + 1);
			getView().setList(widgets);
			return;
		}

		// Save on server
		final Request<Void> changePosition = requestFactory.faveListRequest()
				.rerankFaveItemForCurrentUser(Constants.DEFAULT_HASHTAG, songID, newIndex);
		changePosition.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				// If successfully saved on server, manually set client to match
				final FavePickWidget pickToRank = widgets.get(currentIndex);
				widgets.remove(pickToRank);
				widgets.add(newIndex, pickToRank);
				int i = 1;
				for (final FavePickWidget widget : widgets) {
					widget.setRank(i);
					i++;
				}
				getView().setList(widgets);
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				// TODO: server fail, should do something
			}
		});

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
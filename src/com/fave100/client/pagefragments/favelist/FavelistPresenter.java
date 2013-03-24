package com.fave100.client.pagefragments.favelist;

import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.Notification;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
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

	public interface MyView extends View, HasUiHandlers<FavelistUiHandlers>  {
		void setList(List<FaveItemProxy> list, boolean personalList);
	}

	private ApplicationRequestFactory 	requestFactory;
	private PlaceManager				placeManager;
	// The user whose favelist we are showing
	private AppUserProxy				user;
	// The currently logged in user
	private CurrentUser				currentUser;

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

	public void refreshFavelist() {
		final Request<List<FaveItemProxy>> req = requestFactory.faveListRequest().getFaveList(user.getUsername(), Constants.DEFAULT_HASHTAG);
		req.fire(new Receiver<List<FaveItemProxy>>() {
			@Override
			public void onSuccess(final List<FaveItemProxy> results) {
				final boolean personalList = (currentUser.isLoggedIn() && currentUser.equals(user));
				getView().setList(results, personalList);

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
				if(failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					placeManager.revealPlace(new PlaceRequest(NameTokens.login));
				} else if(failure.getExceptionType().equals(SongLimitReachedException.class.getName())) {
					Notification.show("You cannot have more than 100 songs in list");
				} else if (failure.getExceptionType().equals(SongAlreadyInListException.class.getName())) {
					Notification.show("The song is already in your list");
				}
			}
		});
	}

	@Override
	public void removeSong(final String songID) {
		final Request<Void> req = requestFactory.faveListRequest()
				.removeFaveItemForCurrentUser(Constants.DEFAULT_HASHTAG, songID);
		// TODO: Should add everything client side so need for refresh every time
		req.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				refreshFavelist();
			}
		});
	}

	@Override
	public void editWhyline(final String songID, final String whyline) {
		final Request<Void> editWhyline = requestFactory.faveListRequest()
				.editWhylineForCurrentUser(Constants.DEFAULT_HASHTAG, songID, whyline);
		editWhyline.fire();
	}

	@Override
	public void changeSongPosition(final int oldIndex, final int newIndex) {
		final Request<Void> changePosition = requestFactory.faveListRequest()
				.rerankFaveItemForCurrentUser(Constants.DEFAULT_HASHTAG, oldIndex, newIndex);
		// TODO: Should add everything client side so need for refresh every time
		changePosition.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				refreshFavelist();
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

}
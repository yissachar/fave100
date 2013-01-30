package com.fave100.client.pages.search;

import java.util.List;

import com.fave100.client.Notification;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListRequest;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class SearchPresenter extends
		BasePresenter<SearchPresenter.MyView, SearchPresenter.MyProxy>
		implements SearchUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<SearchUiHandlers> {
		void resetView();

		int getPageNum();

		void setResultCount(int count);

		void setResults(List<SongProxy> resultList);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.search)
	public interface MyProxy extends ProxyPlace<SearchPresenter> {
	}

	public static final int				RESULTS_PER_PAGE	= 25;
	private ApplicationRequestFactory	requestFactory;

	@Inject
	public SearchPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onHide() {
		super.onHide();
		// Set the result list to be blank
		getView().resetView();
	}

	// TODO: need a global "loading" indicator
	@Override
	public void showResults(final String songTerm, final String artistTerm) {

		final int offset = RESULTS_PER_PAGE * (getView().getPageNum() - 1);
		Request<List<SongProxy>> searchReq;
		if (!songTerm.isEmpty()) {
			if (!artistTerm.isEmpty()) {
				searchReq = requestFactory.songRequest().search(songTerm,
						artistTerm, offset);
			} else {
				searchReq = requestFactory.songRequest().searchSong(songTerm,
						offset);
			}
		} else if (!artistTerm.isEmpty()) {
			searchReq = requestFactory.songRequest().searchArtist(artistTerm,
					offset);
		} else {
			return;
		}

		searchReq.fire(new Receiver<List<SongProxy>>() {
			@Override
			public void onSuccess(final List<SongProxy> resultList) {
				getView().setResults(resultList);
				if (resultList.size() > 0) {
					getView()
							.setResultCount(resultList.get(0).getResultCount());
				} else {
					// No result, set to 0
					getView().setResultCount(0);
				}
			}
		});
	}

	@Override
	public void addSong(final SongProxy song) {
		final FaveListRequest faveListRequest = requestFactory
				.faveListRequest();

		final String hashtag = FaveList.DEFAULT_HASHTAG;
		final String id = song.getMbid();

		final Request<Void> addReq = faveListRequest.addFaveItemForCurrentUser(
				hashtag, id, song.getTrackName(), song.getArtistName());

		addReq.fire(new Receiver<Void>() {

			@Override
			public void onSuccess(final Void response) {
				Notification.show("Added");
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				if (failure.getExceptionType().equals(
						SongLimitReachedException.class.getName())) {
					Notification
							.show("You cannot have more than 100 songs in list");
				} else if (failure.getExceptionType().equals(
						SongAlreadyInListException.class.getName())) {
					Notification.show("The song is already in your list");
				}
			}

		});
	}
}

interface SearchUiHandlers extends UiHandlers {
	void showResults(String songTerm, String artistTerm);

	void addSong(SongProxy song);
}

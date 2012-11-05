package com.fave100.client.pages.song;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class SongPresenter extends
		BasePresenter<SongPresenter.MyView, SongPresenter.MyProxy> {

	public interface MyView extends BaseView {
		void setSongInfo(SongProxy song);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.song)
	public interface MyProxy extends ProxyPlace<SongPresenter> {		
	}
	
	private final ApplicationRequestFactory requestFactory;
	private final PlaceManager placeManager;

	@Inject
	public SongPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory,
			final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}
	
	@Override
	public boolean useManualReveal() {
		return true;
	}
	
	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);
		
		// Use parameters to determine what to reveal on page
		final String songId = URL.decode(placeRequest.getParameter("id", ""));	
		if(songId.isEmpty()) {
			// Malformed request, send the user away
			placeManager.revealDefaultPlace();
		} else {
			// Load the song from the datastore
			final Request<SongProxy> getSongReq = requestFactory.songRequest().findSong(songId);
			getSongReq.fire(new Receiver<SongProxy>() {
				@Override
				public void onSuccess(final SongProxy song) {
					getView().setSongInfo(song);
					getProxy().manualReveal(SongPresenter.this);
				}
			});
		}
	}

	@Override
	protected void onBind() {
		super.onBind();
	}
}

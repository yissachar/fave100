package com.fave100.client.widgets;

import java.util.List;

import com.fave100.client.gin.ClientGinjector;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveItemProxy;
import com.fave100.server.domain.favelist.FaveList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class UserThumbList extends Composite {

	private static UserThumbListUiBinder uiBinder = GWT
			.create(UserThumbListUiBinder.class);

	interface UserThumbListUiBinder extends UiBinder<Widget, UserThumbList> {
	}

	@UiField Label username;
	@UiField Image userProfileImage;
	@UiField Label songRank;
	@UiField InlineHyperlink songTitle;
	private List<FaveItemProxy> songList;
	private PlaceManager placeManager;

	public UserThumbList(final ApplicationRequestFactory requestFactory, final AppUserProxy appUser) {
		initWidget(uiBinder.createAndBindUi(this));

		final ClientGinjector ginjector = GWT.create(ClientGinjector.class);
		placeManager = ginjector.getPlaceManager();

		final Request<List<FaveItemProxy>> faveListReq = requestFactory.faveListRequest().getFaveList(appUser.getUsername(), FaveList.DEFAULT_HASHTAG);
		faveListReq.fire(new Receiver<List<FaveItemProxy>>() {
			@Override
			public void onSuccess(final List<FaveItemProxy> songs) {
				songList = songs;
				setRandomSong();
			}
		});

		username.setText(appUser.getUsername());
		userProfileImage.setUrl(appUser.getAvatarImage());
	}

	private void setRandomSong() {
		final int random = (int) (Math.random() * songList.size());
		final FaveItemProxy song = songList.get(random);
		songRank.setText(Integer.toString(random+1));
		songTitle.setText(song.getSong());
		final PlaceRequest placeRequest = new PlaceRequest(NameTokens.song)
											.with("song", song.getSong())
											.with("artist", song.getArtist());
		songTitle.setTargetHistoryToken(placeManager.buildHistoryToken(placeRequest));
	}

}

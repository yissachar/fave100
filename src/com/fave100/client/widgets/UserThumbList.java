package com.fave100.client.widgets;

import java.util.List;

import com.fave100.client.pages.song.SongPresenter;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FaveItemProxy;
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

public class UserThumbList extends Composite {

	private static UserThumbListUiBinder uiBinder = GWT
			.create(UserThumbListUiBinder.class);

	interface UserThumbListUiBinder extends UiBinder<Widget, UserThumbList> {
	}

	@UiField InlineHyperlink username;
	@UiField Image userProfileImage;
	@UiField Label songRank;
	@UiField InlineHyperlink songTitle;
	private List<FaveItemProxy> songList;

	public UserThumbList(final ApplicationRequestFactory requestFactory, final AppUserProxy appUser) {
		initWidget(uiBinder.createAndBindUi(this));

		final Request<List<FaveItemProxy>> faveListReq = requestFactory.faveListRequest().getFaveList(appUser.getUsername(), Constants.DEFAULT_HASHTAG);
		faveListReq.fire(new Receiver<List<FaveItemProxy>>() {
			@Override
			public void onSuccess(final List<FaveItemProxy> songs) {
				songList = songs;
				setRandomSong();
			}
		});

		username.setText(appUser.getUsername());
		final String userPlace = new UrlBuilder(NameTokens.users)
				.with(UsersPresenter.USER_PARAM, appUser.getUsername())
				.getPlaceToken();
		username.setTargetHistoryToken(userPlace);
		userProfileImage.setUrl(appUser.getAvatarImage());
	}

	private void setRandomSong() {
		if (songList.size() == 0)
			return;

		final int random = (int)(Math.random() * songList.size());
		final FaveItemProxy song = songList.get(random);
		songRank.setText(Integer.toString(random + 1));
		songTitle.setText(song.getSong());

		final String songPlace = new UrlBuilder(NameTokens.song)
				.with(SongPresenter.ID_PARAM, song.getSongID())
				.getPlaceToken();
		songTitle.setTargetHistoryToken(songPlace);
	}

}

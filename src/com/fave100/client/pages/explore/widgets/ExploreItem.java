package com.fave100.client.pages.explore.widgets;

import com.fave100.client.pages.song.SongPresenter;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.ExploreResultProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ExploreItem extends Composite {

	private static ExploreItemUiBinder uiBinder = GWT.create(ExploreItemUiBinder.class);

	interface ExploreItemUiBinder extends UiBinder<Widget, ExploreItem> {
	}

	@UiField Image avatar;
	@UiField Hyperlink username;
	@UiField Hyperlink song;
	@UiField Label artist;
	@UiField Label whyline;

	public ExploreItem(final ExploreResultProxy exploreResult) {
		initWidget(uiBinder.createAndBindUi(this));
		// Set values
		avatar.setUrl(exploreResult.getAvatar());
		username.setText(exploreResult.getUsername());
		song.setText(exploreResult.getSong());
		artist.setText(exploreResult.getArtist());
		whyline.setText(exploreResult.getWhyline());

		// Set links
		final String userPlace = new UrlBuilder(NameTokens.users).with(UsersPresenter.USER_PARAM, exploreResult.getUsername()).getPlaceToken();
		username.setTargetHistoryToken(userPlace);

		final String songPlace = new UrlBuilder(NameTokens.song).with(SongPresenter.ID_PARAM, exploreResult.getSongID()).getPlaceToken();
		song.setTargetHistoryToken(songPlace);
	}

}

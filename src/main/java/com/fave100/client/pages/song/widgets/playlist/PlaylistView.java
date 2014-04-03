package com.fave100.client.pages.song.widgets.playlist;

import java.util.List;

import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class PlaylistView extends ViewImpl implements PlaylistPresenter.MyView {

	public interface Binder extends UiBinder<Widget, PlaylistView> {
	}

	@UiField FlowPanel playlistHeader;
	@UiField Anchor _username;
	@UiField Image _avatar;
	@UiField Anchor _hashtag;
	@UiField FlowPanel playlistContainer;

	private final Widget widget;
	private ParameterTokenFormatter _parameterTokenFormatter;

	@Inject
	public PlaylistView(final Binder binder, ParameterTokenFormatter parameterTokenFormatter) {
		widget = binder.createAndBindUi(this);
		_parameterTokenFormatter = parameterTokenFormatter;
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setPlaylist(final List<PlaylistItem> playlistItems) {
		playlistContainer.clear();

		for (final PlaylistItem playlistItem : playlistItems) {
			playlistContainer.add(playlistItem);
		}

	}

	@Override
	public void setUsername(final String username) {
		_username.setText(username);
		_username.setVisible(!username.isEmpty());
	}

	@Override
	public void setHashtag(final String hashtag) {
		_hashtag.setText(hashtag);
		_hashtag.setVisible(!hashtag.isEmpty());
	}

	@Override
	public void setAvatar(final String avatar) {
		_avatar.setUrl(avatar);
		_avatar.setVisible(!avatar.isEmpty());
	}

	@Override
	public void setUrls(final String username, final String hashtag) {
		// Set URL to user's list
		_username.setHref("#" + _parameterTokenFormatter
				.toPlaceToken(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.USER_PARAM, username)
						.with(PlaceParams.LIST_PARAM, hashtag)
						.build()));
		_hashtag.setHref("#" + _parameterTokenFormatter
				.toPlaceToken(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.LIST_PARAM, hashtag)
						.build()));
	}

	@Override
	public void setPlaylistHeight(final int px) {
		final int newHeight = px - playlistHeader.getOffsetHeight();
		playlistContainer.setHeight(newHeight + "px");
	}
}

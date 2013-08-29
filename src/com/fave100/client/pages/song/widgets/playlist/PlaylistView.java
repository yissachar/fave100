package com.fave100.client.pages.song.widgets.playlist;

import java.util.List;

import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.pages.song.SongPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class PlaylistView extends ViewImpl implements PlaylistPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, PlaylistView> {
	}

	@UiField FlowPanel playlistHeader;
	@UiField Anchor _username;
	@UiField Image _avatar;
	@UiField Anchor _hashtag;
	@UiField FlowPanel playlistContainer;

	@Inject
	public PlaylistView(final Binder binder) {
		widget = binder.createAndBindUi(this);
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
		_hashtag.setText("#" + hashtag);
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
		_username.setHref(new UrlBuilder(NameTokens.lists)
				.with(ListPresenter.USER_PARAM, username)
				.with(ListPresenter.LIST_PARAM, hashtag)
				.getUrl());
		//TODO: Is this where we want hashtag url to go?
		_hashtag.setHref(new UrlBuilder(NameTokens.song)
				.with(SongPresenter.LIST_PARAM, hashtag)
				.getUrl());
	}

	@Override
	public void setPlaylistHeight(final int px) {
		final int newHeight = px - playlistHeader.getOffsetHeight();
		playlistContainer.setHeight(newHeight + "px");
	}
}

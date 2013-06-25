package com.fave100.client.pages.song.widgets.playlist;

import java.util.List;

import com.fave100.client.pages.users.UsersPresenter;
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
		_username.setHref(new UrlBuilder(NameTokens.users).with(UsersPresenter.USER_PARAM, username).getUrl());
	}

	@Override
	public void setUrl(final String avatar) {
		_avatar.setUrl(avatar);
	}

	@Override
	public void setPlaylistHeight(final int px) {
		final int newHeight = px - playlistHeader.getOffsetHeight();
		playlistContainer.setHeight(newHeight + "px");
	}
}

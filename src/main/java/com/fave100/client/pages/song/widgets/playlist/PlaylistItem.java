package com.fave100.client.pages.song.widgets.playlist;

import com.fave100.client.events.song.PlaylistSongChangedEvent;
import com.fave100.client.resources.css.GlobalStyle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class PlaylistItem extends Composite {

	private static PlaylistItemUiBinder uiBinder = GWT.create(PlaylistItemUiBinder.class);

	interface PlaylistItemUiBinder extends UiBinder<Widget, PlaylistItem> {
	}

	interface PlaylistStyle extends GlobalStyle {
		String playing();

		String oneHundredth();
	}

	@UiField FocusPanel focusPanel;
	@UiField HTMLPanel container;
	@UiField Label _rank;
	@UiField Label _song;
	@UiField Label _artist;
	@UiField Label _whyline;
	@UiField PlaylistStyle style;
	private String _songID;
	private EventBus eventBus;
	private boolean _currentlyPlaying;

	public PlaylistItem(final EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
	}

	@UiHandler("focusPanel")
	void onClick(final ClickEvent event) {
		eventBus.fireEvent(new PlaylistSongChangedEvent(_songID));
	}

	public void setRank(final int rank) {
		_rank.setText(String.valueOf(rank));
		if (rank == 100) {
			_rank.addStyleName(style.oneHundredth());
		}
	}

	public void setSong(final String song) {
		_song.setText(song);
	}

	public void setArtist(final String artist) {
		_artist.setText(artist);
	}

	public void setWhyline(final String whyline) {
		_whyline.setText(whyline);
	}

	public void setSongID(final String songID) {
		_songID = songID;
	}

	public String getSongID() {
		return _songID;
	}

	public void setCurrentlyPlaying(final boolean playing) {
		_currentlyPlaying = playing;
		if (_currentlyPlaying) {
			container.addStyleName(style.playing());
		}
		else {
			container.removeStyleName(style.playing());
		}
	}

	public boolean isCurrentlyPlaying() {
		return _currentlyPlaying;
	}

}

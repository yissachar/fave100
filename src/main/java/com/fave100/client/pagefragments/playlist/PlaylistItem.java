package com.fave100.client.pagefragments.playlist;

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
	@UiField Label rankText;
	@UiField Label songText;
	@UiField Label artistText;
	@UiField Label whylineText;
	@UiField PlaylistStyle style;
	private int _rank;
	private String _songID;
	private String _list;
	private String _username;
	private EventBus eventBus;
	private boolean _currentlyPlaying;

	public PlaylistItem(final EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
	}

	@UiHandler("focusPanel")
	void onClick(final ClickEvent event) {
		eventBus.fireEvent(new PlaylistSongChangedEvent(_songID, songText.getText(), artistText.getText(), _list, _username, null));
	}

	public int getRank() {
		return _rank;
	}

	public void setRank(final int rank) {
		_rank = rank;
		rankText.setText(String.valueOf(_rank));
		if (_rank == 100) {
			rankText.addStyleName(style.oneHundredth());
		}
	}

	public void setSong(final String song) {
		songText.setText(song);
	}

	public void setArtist(final String artist) {
		artistText.setText(artist);
	}

	public void setWhyline(final String whyline) {
		whylineText.setText(whyline);
	}

	public void setList(final String list) {
		_list = list;
	}

	public void setUsername(final String username) {
		_username = username;
	}

	public void setSongID(final String songID) {
		_songID = songID;
	}

	public String getSongID() {
		return _songID;
	}

	public String getSong() {
		return songText.getText();
	}

	public String getArtist() {
		return artistText.getText();
	}

	public String getList() {
		return _list;
	}

	public String getUsername() {
		return _username;
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

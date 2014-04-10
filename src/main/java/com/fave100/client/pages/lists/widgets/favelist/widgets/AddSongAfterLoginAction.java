package com.fave100.client.pages.lists.widgets.favelist.widgets;

import com.fave100.client.AfterLoginAction;
import com.fave100.client.pages.lists.widgets.favelist.FavelistPresenter;

public class AddSongAfterLoginAction implements AfterLoginAction {

	private FavelistPresenter _favelistPresenter;
	private String _songId;
	private String _song;
	private String _artist;

	public AddSongAfterLoginAction(FavelistPresenter favelistPresenter, String songId, String song, String artist) {
		_favelistPresenter = favelistPresenter;
		_songId = songId;
		_song = song;
		_artist = artist;
	}

	@Override
	public void doAction() {
		_favelistPresenter.addSong(_songId, _song, _artist, false);
	}

}

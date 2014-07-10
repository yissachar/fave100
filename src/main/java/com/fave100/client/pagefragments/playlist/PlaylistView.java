package com.fave100.client.pagefragments.playlist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fave100.client.Utils;
import com.fave100.client.resources.css.AppClientBundle;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.client.widgets.Icon;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class PlaylistView extends ViewWithUiHandlers<PlaylistUiHandlers> implements PlaylistPresenter.MyView {

	public interface Binder extends UiBinder<HTMLPanel, PlaylistView> {
	}

	interface PlaylistStyle extends GlobalStyle {

		String fullScreen();
	}

	@UiField PlaylistStyle style;
	@UiField Panel container;
	@UiField Panel playlist;
	@UiField HTMLPanel youtubePlayer;
	@UiField HTMLPanel whylineView;
	@UiField Panel playlistItemsPanel;
	@UiField Panel playlistControls;
	@UiField Label songName;
	@UiField Label artistName;
	@UiField Button thumbToggle;
	@UiField Button addSongButton;
	@UiField Hyperlink listName;
	@UiField Label byUserText;
	@UiField Hyperlink username;
	@UiField Icon previousButton;
	@UiField Icon nextButton;

	private List<PlaylistItem> _playlistItems;
	private PlaylistItem _playingItem;
	private ParameterTokenFormatter _parameterTokenFormatter;

	@Inject
	PlaylistView(Binder binder, ParameterTokenFormatter parameterTokenFormatter) {
		_parameterTokenFormatter = parameterTokenFormatter;
		initWidget(binder.createAndBindUi(this));

		playlistItemsPanel.setVisible(false);
		playlist.setVisible(false);
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (content == null)
			return;

		if (slot == PlaylistPresenter.YOUTUBE_SLOT) {
			youtubePlayer.clear();
			youtubePlayer.add(content);
		}

		if (slot == PlaylistPresenter.WHYLINE_SLOT) {
			whylineView.clear();
			whylineView.add(content);
		}
	}

	@UiHandler("hideButton")
	void onHideButtonClick(final ClickEvent event) {
		getUiHandlers().stopSong();
		RootPanel.get().removeStyleName(AppClientBundle.INSTANCE.getGlobalCss().playlistVisible());
	}

	@UiHandler("previousButton")
	void onPreviousClick(final ClickEvent event) {
		if (previousButton.isEnabled()) {
			getUiHandlers().previousSong();
		}
	}

	@UiHandler("nextButton")
	void onNextClick(final ClickEvent event) {
		if (nextButton.isEnabled()) {
			getUiHandlers().nextSong();
		}
	}

	@UiHandler("addSongButton")
	void onAddClick(final ClickEvent event) {
		getUiHandlers().addSong();
	}

	@UiHandler("thumbToggle")
	void onThumbToggleClick(ClickEvent event) {
		getUiHandlers().toggleThumbs();
	}

	@Override
	public void playSong(String listName, String username, String song, String artist, String videoId, List<PlaylistItem> playlistItems) {
		playlist.setVisible(true);

		_playlistItems = playlistItems;

		for (PlaylistItem playlistItem : _playlistItems) {
			if (playlistItem.isCurrentlyPlaying()) {
				_playingItem = playlistItem;
			}
		}

		int rank = playlistItems.indexOf(_playingItem) + 1;
		songName.setText(song);
		songName.setTitle(song);
		songName.getElement().getStyle().setColor(Utils.rankToHsl(rank));
		artistName.setText(artist);
		artistName.setTitle(artist);

		Map<String, String> params = new HashMap<String, String>();
		params.put(PlaceParams.LIST_PARAM, listName);

		this.listName.setText(listName);
		this.listName.setTargetHistoryToken(_parameterTokenFormatter
				.toPlaceToken(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(params)
						.build()));

		params.put(PlaceParams.USER_PARAM, username);

		this.username.setText(username);
		this.username.setTargetHistoryToken(_parameterTokenFormatter
				.toPlaceToken(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(params)
						.build()));

		byUserText.setVisible(!username.isEmpty());
		this.listName.setVisible(!listName.isEmpty());

		previousButton.setEnabled(_playingItem.getRank() != 1);
		nextButton.setEnabled(_playingItem.getRank() != playlistItems.size());

		playlistItemsPanel.clear();
		for (PlaylistItem playlistItem : playlistItems) {
			playlistItemsPanel.add(playlistItem);
		}
	}

	@Override
	public void setFullScreen(boolean fullScreen) {
		if (fullScreen) {
			container.addStyleName(style.fullScreen());
		}
		else {
			container.removeStyleName(style.fullScreen());
		}
	}

	@Override
	public void scrollPlayingItemToTop() {

		// First simply scroll it into the view
		_playingItem.getElement().scrollIntoView();

		// Pick an item further down the list and scroll it into view
		PlaylistItem toScroll = null;
		int i = 7;
		while (toScroll == null && i > 0) {
			final int furtherIndex = _playlistItems.indexOf(_playingItem) + i;
			if (furtherIndex < _playlistItems.size())
				toScroll = _playlistItems.get(furtherIndex);
			i--;
		}

		if (toScroll != null) {
			toScroll.getElement().scrollIntoView();
			// At this point our item may be too high up and out of view, so scroll it back into view
			_playingItem.getElement().scrollIntoView();
		}
	}
}

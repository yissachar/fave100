package com.fave100.client.pages.song;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.song.widgets.whyline.WhylineWaterfall;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.SongProxy;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class SongView extends ViewWithUiHandlers<SongUiHandlers>
		implements SongPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, SongView> {
	}

	interface SongViewStyle extends CssResource {
		String playlistVisible();
	}

	@UiField HTMLPanel topBar;
	@UiField Label songTitle;
	@UiField Label artistName;
	@UiField Button addToFave100Button;
	//@UiField YouTubeWidget youTubeWidget;
	@UiField HTMLPanel youTubeWidget;
	@UiField(provided = true) WhylineWaterfall whylineWaterfall;
	@UiField HTMLPanel playlistPane;
	@UiField FlowPanel tabHeaderContainer;
	@UiField Label playlistTabHeader;
	@UiField Label whylineTabHeader;
	@UiField SongViewStyle style;

	@Inject
	public SongView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		whylineWaterfall = new WhylineWaterfall(requestFactory);
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();

			if (content != null) {
				topBar.add(content);
			}
		}

		if (slot == SongPresenter.YOUTUBE_SLOT) {
			youTubeWidget.clear();

			if (content != null) {
				youTubeWidget.add(content);
			}
		}

		if (slot == SongPresenter.PLAYLIST_SLOT) {
			playlistPane.clear();

			if (content != null) {
				playlistPane.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@UiHandler("addToFave100Button")
	void onAddFave100Click(final ClickEvent event) {
		getUiHandlers().addSong();
	}

	@UiHandler("playlistTabHeader")
	void onPlaylistTabHeaderClick(final ClickEvent event) {
		showPlaylist();
	}

	@UiHandler("whylineTabHeader")
	void onWhylineTabHeaderClick(final ClickEvent event) {
		showWhylines();
	}

	@Override
	public void setSongInfo(final SongProxy song) {
		setSongTitle(song.getSong());
		setArtistName(song.getArtist());
		whylineWaterfall.setWhylines(song);
	}

	@Override
	public void setPlaylist(final Boolean visible) {
		if (visible) {
			tabHeaderContainer.addStyleName(style.playlistVisible());
		}
		else {
			tabHeaderContainer.removeStyleName(style.playlistVisible());
		}
	}

	@Override
	public void showPlaylist() {
		whylineWaterfall.show(false);
		playlistPane.setVisible(true);
	}

	@Override
	public void setWhylineHeight(final int px) {
		whylineWaterfall.setHeight(px + "px");
	}

	@Override
	public void showWhylines() {
		playlistPane.setVisible(false);
		whylineWaterfall.show(true);
	}

	private void setSongTitle(final String title) {
		songTitle.setText(title);
	}

	private void setArtistName(final String name) {
		artistName.setText(name);
	}

	@Override
	public void scrollYouTubeIntoView() {
		songTitle.getElement().scrollIntoView();
	}

	@Override
	public void clearWhylines() {
		whylineWaterfall.clearWhylines();
	}

}

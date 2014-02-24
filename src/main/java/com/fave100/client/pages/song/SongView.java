package com.fave100.client.pages.song;

import com.fave100.client.generated.entities.FaveItem;
import com.fave100.client.pages.BasePresenter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
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

	@UiField HTMLPanel topBar;
	@UiField Label songTitle;
	@UiField Label artistName;
	@UiField Button addToFave100Button;
	@UiField HTMLPanel songContainer;
	@UiField HTMLPanel youTubeWidget;
	@UiField HTMLPanel whylineWidget;
	@UiField HTMLPanel playlistPanel;

	@Inject
	public SongView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		super.setInSlot(slot, content);

		if (content == null)
			return;

		if (slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();
			topBar.add(content);
		}

		if (slot == SongPresenter.YOUTUBE_SLOT) {
			youTubeWidget.clear();
			youTubeWidget.add(content);
		}

		if (slot == SongPresenter.WHYLINE_SLOT) {
			whylineWidget.clear();

			if (content != null) {
				whylineWidget.add(content);
			}
		}

		if (slot == SongPresenter.PLAYLIST_SLOT) {
			playlistPanel.clear();
			playlistPanel.add(content);
		}
	}

	@UiHandler("addToFave100Button")
	void onAddFave100Click(final ClickEvent event) {
		getUiHandlers().addSong();
	}

	@Override
	public void setSongInfo(final FaveItem song) {
		setSongTitle(song.getSong());
		setArtistName(song.getArtist());
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
	public int getSongContainerHeight() {
		return songContainer.getOffsetHeight();
	}

}

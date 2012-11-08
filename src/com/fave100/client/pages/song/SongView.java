package com.fave100.client.pages.song;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.client.widgets.WhylineWaterfall;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class SongView extends ViewImpl implements SongPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, SongView> {
	}

	@UiField(provided = true) WhylineWaterfall whylineWaterfall;
	@UiField HTMLPanel topBar;
	@UiField Label songTitle;
	@UiField Label artistName;
	@UiField Label releaseDate;
	@UiField Frame youTubePlayer;

	@Inject
	public SongView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		whylineWaterfall = new WhylineWaterfall(requestFactory);
		widget = binder.createAndBindUi(this);		
		youTubePlayer.setVisible(false);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@Override
	public void setInSlot(final Object slot, final Widget content) {
		if(slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();
			
			if(content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}
	
	@Override
	public void setSongInfo(final SongProxy song) {
		setSongTitle(song.getTrackName());
		setArtistName(song.getArtistName());
		setReleaseDate(song.getReleaseDate());
		final String url = song.getYouTubeEmbedUrl(); 
		if(url != null && !url.isEmpty()) {
			youTubePlayer.setUrl(url);
			youTubePlayer.setVisible(true);
		}
	}

	private void setSongTitle(final String title) {
		songTitle.setText(title);
	}
	
	private void setArtistName(final String name) {
		artistName.setText(name);
	}

	private void setReleaseDate(final String date) {
		releaseDate.setText(date);
	}	
}

package com.fave100.client.pages.song;

import com.fave100.client.pages.song.widgets.youtube.YouTubePresenter;
import com.fave100.client.pages.song.widgets.youtube.YouTubeView;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class SongModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		bindPresenter(SongPresenter.class, SongPresenter.MyView.class, SongView.class, SongPresenter.MyProxy.class);
		bindSingletonPresenterWidget(YouTubePresenter.class, YouTubePresenter.MyView.class, YouTubeView.class);
	}

}

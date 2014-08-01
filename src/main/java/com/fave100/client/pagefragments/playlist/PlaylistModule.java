package com.fave100.client.pagefragments.playlist;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class PlaylistModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(PlaylistPresenter.class, PlaylistPresenter.MyView.class, PlaylistView.class);
	}
}

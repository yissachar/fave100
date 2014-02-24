package com.fave100.client.pagefragments.popups.addsong;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class AddSongModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(AddSongPresenter.class, AddSongPresenter.MyView.class, AddSongView.class);
	}
}

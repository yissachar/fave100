package com.fave100.client.pages.lists.widgets.addsongsearch;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class AddSongSearchModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(AddSongSearchPresenter.class, AddSongSearchPresenter.MyView.class, AddSongSearchView.class);
	}
}

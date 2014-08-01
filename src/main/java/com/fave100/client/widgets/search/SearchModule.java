package com.fave100.client.widgets.search;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.fave100.client.pages.lists.widgets.addsongsearch.AddSongSearchModule;

public class SearchModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new AddSongSearchModule());
		bindPresenterWidget(SearchPresenter.class, SearchPresenter.MyView.class, SearchView.class);
	}
}

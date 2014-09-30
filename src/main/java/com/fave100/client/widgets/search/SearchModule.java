package com.fave100.client.widgets.search;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class SearchModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenterWidget(SearchPresenter.class, SearchPresenter.MyView.class, SearchView.class);
	}
}

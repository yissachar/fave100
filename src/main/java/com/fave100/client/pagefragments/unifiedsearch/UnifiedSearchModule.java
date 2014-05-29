package com.fave100.client.pagefragments.unifiedsearch;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class UnifiedSearchModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(UnifiedSearchPresenter.class, UnifiedSearchPresenter.MyView.class, UnifiedSearchView.class);
	}
}

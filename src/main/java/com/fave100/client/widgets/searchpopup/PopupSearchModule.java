package com.fave100.client.widgets.searchpopup;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class PopupSearchModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(PopupSearchPresenter.class, PopupSearchPresenter.MyView.class, PopupSearchView.class);
	}
}

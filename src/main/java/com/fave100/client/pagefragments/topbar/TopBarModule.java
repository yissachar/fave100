package com.fave100.client.pagefragments.topbar;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class TopBarModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		bindSingletonPresenterWidget(TopBarPresenter.class, TopBarPresenter.MyView.class, TopBarView.class);
	}

}

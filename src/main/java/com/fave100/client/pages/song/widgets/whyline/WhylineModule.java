package com.fave100.client.pages.song.widgets.whyline;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class WhylineModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(WhylinePresenter.class, WhylinePresenter.MyView.class, WhylineView.class);
	}
}

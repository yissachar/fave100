package com.fave100.client.pages.song.widgets.whyline;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.fave100.client.pagefragments.login.aboutpopup.AboutPopupModule;

public class WhylineModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new AboutPopupModule());
		bindSingletonPresenterWidget(WhylinePresenter.class, WhylinePresenter.MyView.class, WhylineView.class);
	}
}

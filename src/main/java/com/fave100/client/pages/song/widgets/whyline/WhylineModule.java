package com.fave100.client.pages.song.widgets.whyline;

import com.fave100.client.pagefragments.login.aboutpopup.AboutPopupModule;
import com.fave100.client.widgets.alert.AlertModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.fave100.client.pages.about.AboutModule;

public class WhylineModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new AboutModule());
		install(new AlertModule());
		install(new AboutPopupModule());
		bindSingletonPresenterWidget(WhylinePresenter.class, WhylinePresenter.MyView.class, WhylineView.class);
	}
}

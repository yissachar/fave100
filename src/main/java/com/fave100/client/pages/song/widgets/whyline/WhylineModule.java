package com.fave100.client.pages.song.widgets.whyline;

import com.fave100.client.pages.about.AboutModule;
import com.fave100.client.pages.tour.TourModule;
import com.fave100.client.widgets.alert.AlertModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class WhylineModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new TourModule());
		install(new AboutModule());
		install(new AlertModule());
		bindSingletonPresenterWidget(WhylinePresenter.class, WhylinePresenter.MyView.class, WhylineView.class);
	}
}

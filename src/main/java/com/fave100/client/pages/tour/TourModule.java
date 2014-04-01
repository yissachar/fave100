package com.fave100.client.pages.tour;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class TourModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenter(TourPresenter.class, TourPresenter.MyView.class, TourView.class, TourPresenter.MyProxy.class);
	}
}

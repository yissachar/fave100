package com.fave100.client.pagefragments.login.aboutpopup;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class AboutPopupModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(AboutPopupPresenter.class, AboutPopupPresenter.MyView.class, AboutPopupView.class);
	}
}

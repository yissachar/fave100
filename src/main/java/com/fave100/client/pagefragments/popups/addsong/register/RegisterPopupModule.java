package com.fave100.client.pagefragments.popups.addsong.register;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class RegisterPopupModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(RegisterPopupPresenter.class, RegisterPopupPresenter.MyView.class, RegisterPopupView.class);
	}
}

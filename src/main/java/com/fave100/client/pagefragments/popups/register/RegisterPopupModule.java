package com.fave100.client.pagefragments.popups.register;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class RegisterPopupModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(RegisterPopupPresenter.class, RegisterPopupPresenter.MyView.class, RegisterPopupView.class);
	}
}

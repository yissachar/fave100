package com.fave100.client.widgets.alert;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class AlertModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenterWidget(AlertPresenter.class, AlertPresenter.MyView.class, AlertView.class);
	}
}

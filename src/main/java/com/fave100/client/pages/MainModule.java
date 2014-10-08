package com.fave100.client.pages;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.fave100.client.pages.admin.AdminModule;

public class MainModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new AdminModule());
		bindPresenter(MainPresenter.class, MainPresenter.MyView.class, MainView.class, MainPresenter.MyProxy.class);
	}
}

package com.fave100.client.pages.listbrowser;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class ListBrowserModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenter(ListBrowserPresenter.class, ListBrowserPresenter.MyView.class, ListBrowserView.class, ListBrowserPresenter.MyProxy.class);
	}
}

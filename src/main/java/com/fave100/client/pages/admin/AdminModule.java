package com.fave100.client.pages.admin;

import com.fave100.client.widgets.searchpopup.PopupSearchPresenter;
import com.fave100.client.widgets.searchpopup.PopupSearchView;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class AdminModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenter(AdminPresenter.class, AdminPresenter.MyView.class, AdminView.class, AdminPresenter.MyProxy.class);
		bindPresenterWidget(PopupSearchPresenter.class, PopupSearchPresenter.MyView.class, PopupSearchView.class);
	}
}
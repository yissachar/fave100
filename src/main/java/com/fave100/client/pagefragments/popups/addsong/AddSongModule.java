package com.fave100.client.pagefragments.popups.addsong;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.fave100.client.pagefragments.popups.register.RegisterPopupModule;

public class AddSongModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new RegisterPopupModule());
		bindSingletonPresenterWidget(AddSongPresenter.class, AddSongPresenter.MyView.class, AddSongView.class);
	}
}

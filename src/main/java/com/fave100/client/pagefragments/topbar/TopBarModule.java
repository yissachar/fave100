package com.fave100.client.pagefragments.topbar;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.fave100.client.pagefragments.playlist.PlaylistModule;

public class TopBarModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		install(new PlaylistModule());
		bindSingletonPresenterWidget(TopBarPresenter.class, TopBarPresenter.MyView.class, TopBarView.class);
	}

}

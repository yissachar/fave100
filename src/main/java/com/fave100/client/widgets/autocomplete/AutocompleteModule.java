package com.fave100.client.widgets.autocomplete;

import com.fave100.client.pagefragments.popups.addsong.AddSongModule;
import com.fave100.client.pages.song.widgets.whyline.WhylineModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class AutocompleteModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new WhylineModule());
		install(new AddSongModule());
		bindPresenterWidget(AutocompletePresenter.class, AutocompletePresenter.MyView.class, AutocompleteView.class);
	}
}

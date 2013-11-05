package com.fave100.client.pages.lists.widgets.autocomplete.list;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.fave100.client.pagefragments.popups.addsong.addsong.AddSongModule;
import com.fave100.client.pages.song.widgets.whyline.WhylineModule;

public class ListAutocompleteModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new WhylineModule());
		install(new AddSongModule());
		bindSingletonPresenterWidget(ListAutocompletePresenter.class, ListAutocompletePresenter.MyView.class, ListAutocompleteView.class);
	}
}

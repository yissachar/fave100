package com.fave100.client.pages.lists.widgets.autocomplete.list;

import com.fave100.client.pagefragments.popups.addsong.AddSongModule;
import com.fave100.client.pages.song.widgets.whyline.WhylineModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class ListAutocompleteModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new WhylineModule());
		install(new AddSongModule());
		bindPresenterWidget(ListAutocompletePresenter.class, ListAutocompletePresenter.MyView.class, ListAutocompleteView.class);
	}
}

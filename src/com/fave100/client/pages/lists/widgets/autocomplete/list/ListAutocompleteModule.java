package com.fave100.client.pages.lists.widgets.autocomplete.list;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.fave100.client.pagefragments.popups.addsong.addsong.AddSongModule;

public class ListAutocompleteModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new AddSongModule());
		bindSingletonPresenterWidget(ListAutocompletePresenter.class, ListAutocompletePresenter.MyView.class, ListAutocompleteView.class);
	}
}

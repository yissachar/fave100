package com.fave100.client.pages.lists.widgets.autocomplete.list;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class ListAutocompleteModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(ListAutocompletePresenter.class, ListAutocompletePresenter.MyView.class, ListAutocompleteView.class);
	}
}

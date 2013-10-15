package com.fave100.client.pages.lists.widgets.listmanager.widgets.autocomplete;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class AddListAutocompleteModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(AddListAutocompletePresenter.class, AddListAutocompletePresenter.MyView.class, AddListAutocompleteView.class);
	}
}

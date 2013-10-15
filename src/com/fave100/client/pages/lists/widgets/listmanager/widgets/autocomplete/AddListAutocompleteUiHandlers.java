package com.fave100.client.pages.lists.widgets.listmanager.widgets.autocomplete;

import com.gwtplatform.mvp.client.UiHandlers;

public interface AddListAutocompleteUiHandlers extends UiHandlers {
	void getAutocompleteResults(String searchTerm);

	int getSelection();

	void setSelection(int position, boolean relative);

	void listSelected();
}

package com.fave100.client.pages.lists.widgets.autocomplete.list;

import com.gwtplatform.mvp.client.UiHandlers;

public interface ListAutocompleteUiHandlers extends UiHandlers {
	void getAutocompleteResults(String searchTerm);

	int getSelection();

	void setSelection(int position, boolean relative);

	void listSelected();
}

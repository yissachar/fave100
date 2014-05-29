package com.fave100.client.widgets.autocomplete;

import com.gwtplatform.mvp.client.UiHandlers;

public interface AutocompleteUiHandlers extends UiHandlers {
	void getAutocompleteResults(String searchTerm);

	int getSelection();

	void setSelection(int position, boolean relative);

	void suggestionSelected();
}

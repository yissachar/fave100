package com.fave100.client.pagefragments.unifiedsearch;

import com.gwtplatform.mvp.client.UiHandlers;

public interface UnifiedSearchUiHandlers extends UiHandlers {
	void getSearchResults(String searchTerm);

	void clearSearchResults();

	int getSelection();

	void setSelection(int position);

	int getMaxSelection();

	void deselect();

	void incrementSelection();

	void decrementSelection();

	void loadMore();

	void setSearchType(SearchType searchType);

	void selectSuggestion();

	void setAddMode(boolean addMode);

	String getHelpText();

	int getTotalResults();
}

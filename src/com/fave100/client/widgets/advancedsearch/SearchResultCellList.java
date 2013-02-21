package com.fave100.client.widgets.advancedsearch;

import com.fave100.client.pages.search.SearchUiHandlers;
import com.fave100.shared.requestfactory.SongProxy;
import com.google.gwt.user.cellview.client.CellList;

public class SearchResultCellList extends CellList<SongProxy> {

	public SearchResultCellList(final SearchResultCell cell) {
		super(cell);
	}

	public void setCellUiHandlers(final SearchUiHandlers uiHandlers) {
		((SearchResultCell) getCell()).setUiHandlers(uiHandlers);
	}


}

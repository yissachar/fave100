package com.fave100.client.widgets.searchpopup;

import com.fave100.client.widgets.search.SearchCompletedAction;

public class PopupSearchCompletedAction implements SearchCompletedAction {

	private PopupSearchPresenter.MyView _addSongSearchView;

	public PopupSearchCompletedAction(PopupSearchPresenter.MyView addSongSearchView) {
		_addSongSearchView = addSongSearchView;
	}

	@Override
	public void execute() {
		_addSongSearchView.reposition();
	}

}

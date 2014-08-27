package com.fave100.client.pages.lists.widgets.addsongsearch;

import com.fave100.client.widgets.search.SearchCompletedAction;

public class AddSongSearchCompletedAction implements SearchCompletedAction {

	private AddSongSearchPresenter.MyView _addSongSearchView;

	public AddSongSearchCompletedAction(AddSongSearchPresenter.MyView addSongSearchView) {
		_addSongSearchView = addSongSearchView;
	}

	@Override
	public void execute() {
		_addSongSearchView.reposition();
	}

}

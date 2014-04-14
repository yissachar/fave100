package com.fave100.client.pages.lists.widgets.globallistdetails;

import com.fave100.client.AfterLoginAction;

public class AddListAfterLoginAction implements AfterLoginAction {

	private GlobalListDetailsPresenter _globalListDetailsPresenter;
	private String _listName;

	public AddListAfterLoginAction(GlobalListDetailsPresenter globalListDetailsPresenter, String listName) {
		_globalListDetailsPresenter = globalListDetailsPresenter;
		_listName = listName;
	}

	@Override
	public void doAction() {
		_globalListDetailsPresenter.setHashtag(_listName);
		_globalListDetailsPresenter.contributeToList();
	}

}

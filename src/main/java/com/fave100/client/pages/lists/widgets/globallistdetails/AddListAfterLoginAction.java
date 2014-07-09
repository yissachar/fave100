package com.fave100.client.pages.lists.widgets.globallistdetails;

import com.fave100.client.AfterLoginAction;
import com.fave100.client.pages.lists.ListPresenter;

public class AddListAfterLoginAction implements AfterLoginAction {

	private ListPresenter _listPresenter;

	public AddListAfterLoginAction(ListPresenter listPresenter) {
		_listPresenter = listPresenter;
	}

	@Override
	public void doAction() {
		_listPresenter.contributeToList();
	}

}

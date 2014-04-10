package com.fave100.client.pages.lists.widgets.globallistdetails;

import com.fave100.client.AfterLoginAction;
import com.fave100.client.CurrentUser;

public class AddListAfterLoginAction implements AfterLoginAction {

	private String _listName;
	private CurrentUser _currentUser;

	public AddListAfterLoginAction(CurrentUser currentUser, String listName) {
		_currentUser = currentUser;
		_listName = listName;
	}

	@Override
	public void doAction() {
		_currentUser.addFaveList(_listName);
	}

}

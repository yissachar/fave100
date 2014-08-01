package com.fave100.client;

import com.fave100.client.generated.entities.AppUser;

public class FollowUserAfterLoginAction implements AfterLoginAction {

	private CurrentUser _currentUser;
	private AppUser _userToFollow;

	public FollowUserAfterLoginAction(CurrentUser currentUser, AppUser userToFollow) {
		_currentUser = currentUser;
		_userToFollow = userToFollow;
	}

	@Override
	public void doAction() {
		_currentUser.followUser(_userToFollow);
	}

}

package com.fave100.server;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.fave100.server.api.AuthApi;
import com.fave100.server.domain.UserRegistration;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.shared.Constants;

public class TestHelper {

	public static final String SHOULD_THROW_EXCEPTION_MSG = "Should throw an exception";
	// This is used for mocking, we need to pass in the method name for Mockito to properly stub it
	public static final String GET_LOGGED_IN_USER_METHOD_NAME = "getLoggedInUser";

	public static HttpServletRequest newReq() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(req.getSession()).thenReturn(session);
		return req;
	}

	public static AppUser createLoggedOutUser(String username, String password, String email) {
		HttpServletRequest req = TestHelper.newReq();
		AppUser createdAppUser = AuthApi.createAppUser(req, new UserRegistration(username, password, email));
		AuthApi.logout(req);
		return createdAppUser;
	}

	public static void addSingleFaveItemToDefaultList(AppUser user, String songId) {
		FaveList faveList = FaveListDao.findFaveList(user.getUsername(), Constants.DEFAULT_HASHTAG);
		FaveItem faveItem = new FaveItem("asdf", "1234f", songId);
		faveList.getList().add(faveItem);
		ofy().save().entity(faveList).now();
	}

}

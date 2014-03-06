package com.fave100.server.api;

import static org.junit.Assert.assertNull;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fave100.server.TestHelper;
import com.fave100.server.domain.UserRegistration;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.server.domain.favelist.Hashtag;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UserApi.class)
public class FaveListDeletionTest {

	static {
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(FaveList.class);
		ObjectifyService.register(EmailID.class);
		ObjectifyService.register(Hashtag.class);
		ObjectifyService.register(Whyline.class);
	}

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(100));
	private AppUser loggedInUser;
	private HttpServletRequest req;

	@Before
	public void setUp() throws BadRequestException {
		helper.setUp();
		// Create a user
		String username = "tester";

		PowerMockito.spy(UserApi.class);

		req = TestHelper.newReq();

		loggedInUser = AuthApi.createAppUser(req, new UserRegistration(username, "goodtests", "testuser@example.com"));
		PowerMockito.stub(PowerMockito.method(UserApi.class, TestHelper.GET_LOGGED_IN_USER_METHOD_NAME)).toReturn(loggedInUser);
	}

	@After
	public void tearDown() {
		ObjectifyFilter.complete();
		helper.tearDown();
		loggedInUser = null;
	}

	@Test
	public void should_delete_favelist() throws BadRequestException, UnauthorizedException, ForbiddenException {
		// Create a favelist
		String faveListName = "favelisttodelete";
		UserApi.addFaveListForCurrentUser(req, faveListName);

		// Delete it
		UserApi.deleteFaveListForCurrentUser(req, faveListName);

		// Favelist no longer exists in datastore
		assertNull("Deleted FaveList must no longer exist in datastore", FaveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}
}
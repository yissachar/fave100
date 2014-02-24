package com.fave100.server;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.api.AppUserApi;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.appuser.Following;
import com.fave100.server.domain.appuser.FollowingResult;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.fave100.shared.exceptions.user.UserNotFoundException;
import com.fave100.shared.exceptions.user.UsernameAlreadyExistsException;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;

public class FollowingTest {

	static {
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(FaveList.class);
		ObjectifyService.register(EmailID.class);
		ObjectifyService.register(Hashtag.class);
		ObjectifyService.register(Following.class);
	}

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(100));
	private AppUserDao appUserDao;
	private AppUserApi appUserApi;

	@Before
	public void setUp() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException, NotLoggedInException, UserNotFoundException {
		helper.setUp();
		// Create a user
		appUserDao = new AppUserDao();
		appUserApi = new AppUserApi(appUserDao);

		AppUserDao mockAppUserDao = spy(new AppUserDao());
		appUserDao = mockAppUserDao;

		AppUserApi mockAppUserApi = spy(new AppUserApi(appUserDao));
		appUserApi = mockAppUserApi;
	}

	@After
	public void tearDown() {
		ObjectifyFilter.complete();
		helper.tearDown();
		appUserDao = null;
	}

	@Test
	public void followUserTest() throws BadRequestException, ForbiddenException, UnauthorizedException, NotFoundException {
		HttpServletRequest req = TestHelper.newReq();

		AppUser loggedInUser = appUserApi.createAppUser(req, "tester", "goodtests", "testuser@example.com").getAppUser();
		doReturn(loggedInUser).when(appUserApi).getLoggedInAppUser(req);

		AppUser userToFollow = appUserApi.createAppUser(TestHelper.newReq(), "john", "passpass31", "lemmings@example.com").getAppUser();
		appUserApi.followUser(req, userToFollow.getUsername());
		FollowingResult followingResult = appUserApi.getFollowing(req, loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);
		assertTrue(followingResult.getFollowing().contains(userToFollow));
	}

	@Test
	public void followMultipleUsersTest() throws BadRequestException, ForbiddenException, UnauthorizedException, NotFoundException {
		HttpServletRequest req = TestHelper.newReq();

		AppUser loggedInUser = appUserApi.createAppUser(req, "tester2", "goodtests", "testuser2@example.com").getAppUser();
		doReturn(loggedInUser).when(appUserApi).getLoggedInAppUser(req);

		AppUser userToFollow1 = appUserApi.createAppUser(TestHelper.newReq(), "bob", "passpass31", "followuser@example.com").getAppUser();
		AppUser userToFollow2 = appUserApi.createAppUser(TestHelper.newReq(), "derek", "xcvb1sdf1", "anotheruser@example.com").getAppUser();

		appUserApi.followUser(req, userToFollow1.getUsername());
		appUserApi.followUser(req, userToFollow2.getUsername());

		FollowingResult followingResult = appUserApi.getFollowing(req, loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 2);
		assertTrue(followingResult.getFollowing().contains(userToFollow1));
		assertTrue(followingResult.getFollowing().contains(userToFollow2));
	}

	@Test
	public void followUserCaseInsensitiveTest() throws BadRequestException, ForbiddenException, UnauthorizedException, NotFoundException {
		HttpServletRequest req = TestHelper.newReq();

		AppUser loggedInUser = appUserApi.createAppUser(req, "tester3", "goodtests", "testuser3@example.com").getAppUser();
		doReturn(loggedInUser).when(appUserApi).getLoggedInAppUser(req);

		AppUser userToFollow = appUserApi.createAppUser(TestHelper.newReq(), "MIKE", "bcv13zxcg", "foobar@example.com").getAppUser();
		appUserApi.followUser(req, userToFollow.getUsername());
		FollowingResult followingResult = appUserApi.getFollowing(req, loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);
		assertTrue(followingResult.getFollowing().contains(userToFollow));
	}

	@Test
	public void unfollowUserTest() throws BadRequestException, ForbiddenException, UnauthorizedException, NotFoundException {
		HttpServletRequest req = TestHelper.newReq();

		AppUser loggedInUser = appUserApi.createAppUser(req, "tester4", "goodtests", "testuser4@example.com").getAppUser();
		doReturn(loggedInUser).when(appUserApi).getLoggedInAppUser(req);

		// Follow
		AppUser userToFollow = appUserApi.createAppUser(TestHelper.newReq(), "liam", "bv1xcvaw46", "booj@example.com").getAppUser();
		appUserApi.followUser(req, userToFollow.getUsername());
		FollowingResult followingResult = appUserApi.getFollowing(req, loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);

		// Unfollow
		appUserApi.unfollowUser(req, userToFollow.getUsername());
		followingResult = appUserApi.getFollowing(req, loggedInUser.getUsername(), 0);
		assertTrue(followingResult.getFollowing().size() == 0);
	}

	@Test
	public void unfollowUserCaseInsensitiveTest() throws BadRequestException, ForbiddenException, UnauthorizedException, NotFoundException {
		HttpServletRequest req = TestHelper.newReq();

		AppUser loggedInUser = appUserApi.createAppUser(req, "tester5", "goodtests", "testuser5@example.com").getAppUser();
		doReturn(loggedInUser).when(appUserApi).getLoggedInAppUser(req);

		// Follow
		AppUser userToFollow = appUserApi.createAppUser(TestHelper.newReq(), "KRING", "awetzx14sva", "kiasd@example.com").getAppUser();
		appUserApi.followUser(req, userToFollow.getUsername());
		FollowingResult followingResult = appUserApi.getFollowing(req, loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);

		// Unfollow
		appUserApi.unfollowUser(req, userToFollow.getUsername());
		followingResult = appUserApi.getFollowing(req, loggedInUser.getUsername(), 0);
		assertTrue(followingResult.getFollowing().size() == 0);
	}
}
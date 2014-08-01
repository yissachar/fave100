package com.fave100.server.api;

import static org.junit.Assert.assertTrue;

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
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.appuser.Following;
import com.fave100.server.domain.appuser.FollowingResult;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.Hashtag;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UserApi.class)
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
	private AppUser loggedInUser;
	private HttpServletRequest req;

	@Before
	public void setUp() {
		helper.setUp();

		PowerMockito.spy(UserApi.class);

		req = TestHelper.newReq();

		UserRegistration registration = new UserRegistration();
		registration.setUsername("tester");
		registration.setPassword("goodtests");
		registration.setEmail("testuser@example.com");

		loggedInUser = AuthApi.createAppUser(req, registration);
	}

	@After
	public void tearDown() {
		ObjectifyFilter.complete();
		helper.tearDown();
		loggedInUser = null;
	}

	@Test
	public void should_follow_user() {
		HttpServletRequest req = TestHelper.newReq();

		UserRegistration registration = new UserRegistration();
		registration.setUsername("john");
		registration.setPassword("passpass31");
		registration.setEmail("lemmings@example.com");

		AppUser userToFollow = AuthApi.createAppUser(TestHelper.newReq(), registration);
		PowerMockito.stub(PowerMockito.method(UserApi.class, TestHelper.GET_LOGGED_IN_USER_METHOD_NAME)).toReturn(loggedInUser);

		UserApi.followUser(UserApi.getLoggedInUser(req), userToFollow.getUsername());

		FollowingResult followingResult = UsersApi.getFollowing(UserApi.getLoggedInUser(req), loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);
		assertTrue(followingResult.getFollowing().contains(userToFollow));
	}

	@Test
	public void should_follow_multiple_users() {
		HttpServletRequest req = TestHelper.newReq();

		AppUser loggedInUser = AuthApi.createAppUser(req, new UserRegistration("tester2", "goodtests", "testuser2@example.com"));
		PowerMockito.stub(PowerMockito.method(UserApi.class, TestHelper.GET_LOGGED_IN_USER_METHOD_NAME)).toReturn(loggedInUser);

		AppUser userToFollow1 = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("bob", "passpass31", "followuser@example.com"));
		AppUser userToFollow2 = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("derek", "xcvb1sdf1", "anotheruser@example.com"));

		UserApi.followUser(UserApi.getLoggedInUser(req), userToFollow1.getUsername());
		UserApi.followUser(UserApi.getLoggedInUser(req), userToFollow2.getUsername());

		FollowingResult followingResult = UsersApi.getFollowing(UserApi.getLoggedInUser(req), loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 2);
		assertTrue(followingResult.getFollowing().contains(userToFollow1));
		assertTrue(followingResult.getFollowing().contains(userToFollow2));
	}

	@Test
	public void should_follow_user_regardless_of_name_case() {
		HttpServletRequest req = TestHelper.newReq();

		AppUser loggedInUser = AuthApi.createAppUser(req, new UserRegistration("tester3", "goodtests", "testuser3@example.com"));
		PowerMockito.stub(PowerMockito.method(UserApi.class, TestHelper.GET_LOGGED_IN_USER_METHOD_NAME)).toReturn(loggedInUser);

		AppUser userToFollow = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("MIKE", "bcv13zxcg", "foobar@example.com"));
		UserApi.followUser(UserApi.getLoggedInUser(req), userToFollow.getUsername());
		FollowingResult followingResult = UsersApi.getFollowing(UserApi.getLoggedInUser(req), loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);
		assertTrue(followingResult.getFollowing().contains(userToFollow));
	}

	@Test
	public void should_unfollow_user() {
		HttpServletRequest req = TestHelper.newReq();

		AppUser loggedInUser = AuthApi.createAppUser(req, new UserRegistration("tester4", "goodtests", "testuser4@example.com"));
		PowerMockito.stub(PowerMockito.method(UserApi.class, TestHelper.GET_LOGGED_IN_USER_METHOD_NAME)).toReturn(loggedInUser);

		// Follow
		AppUser userToFollow = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("liam", "bv1xcvaw46", "booj@example.com"));
		UserApi.followUser(UserApi.getLoggedInUser(req), userToFollow.getUsername());
		FollowingResult followingResult = UsersApi.getFollowing(UserApi.getLoggedInUser(req), loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);

		// Unfollow
		UserApi.unfollowUser(UserApi.getLoggedInUser(req), userToFollow.getUsername());
		followingResult = UsersApi.getFollowing(UserApi.getLoggedInUser(req), loggedInUser.getUsername(), 0);
		assertTrue(followingResult.getFollowing().size() == 0);
	}

	@Test
	public void should_unfollow_user_regardless_of_name_case() {
		HttpServletRequest req = TestHelper.newReq();

		AppUser loggedInUser = AuthApi.createAppUser(req, new UserRegistration("tester5", "goodtests", "testuser5@example.com"));
		PowerMockito.stub(PowerMockito.method(UserApi.class, TestHelper.GET_LOGGED_IN_USER_METHOD_NAME)).toReturn(loggedInUser);

		// Follow
		AppUser userToFollow = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("KRING", "awetzx14sva", "kiasd@example.com"));
		UserApi.followUser(UserApi.getLoggedInUser(req), userToFollow.getUsername());
		FollowingResult followingResult = UsersApi.getFollowing(UserApi.getLoggedInUser(req), loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);

		// Unfollow
		UserApi.unfollowUser(UserApi.getLoggedInUser(req), userToFollow.getUsername());
		followingResult = UsersApi.getFollowing(UserApi.getLoggedInUser(req), loggedInUser.getUsername(), 0);
		assertTrue(followingResult.getFollowing().size() == 0);
	}
}
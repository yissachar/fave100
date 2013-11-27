package com.fave100.server;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.appuser.Following;
import com.fave100.server.domain.appuser.FollowingResult;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.shared.exceptions.following.AlreadyFollowingException;
import com.fave100.shared.exceptions.following.CannotFollowYourselfException;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.fave100.shared.exceptions.user.UserNotFoundException;
import com.fave100.shared.exceptions.user.UsernameAlreadyExistsException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
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

	@Before
	public void setUp() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException, NotLoggedInException, UserNotFoundException {
		helper.setUp();
		// Create a user
		appUserDao = new AppUserDao();

		AppUserDao mockAppUserDao = spy(new AppUserDao());
		appUserDao = mockAppUserDao;
	}

	@After
	public void tearDown() {
		helper.tearDown();
		appUserDao = null;
	}

	@Test
	public void followUserTest() throws NotLoggedInException, CannotFollowYourselfException, AlreadyFollowingException, UserNotFoundException, UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		AppUser loggedInUser = appUserDao.createAppUser("tester", "goodtests", "testuser@example.com");
		doReturn(loggedInUser).when(appUserDao).getLoggedInAppUser();

		AppUser userToFollow = appUserDao.createAppUser("john", "passpass31", "lemmings@example.com");
		appUserDao.followUser(userToFollow.getUsername());
		FollowingResult followingResult = appUserDao.getFollowing(loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);
		assertTrue(followingResult.getFollowing().contains(userToFollow));
	}

	@Test
	public void followMultipleUsersTest() throws NotLoggedInException, CannotFollowYourselfException, AlreadyFollowingException, UserNotFoundException, UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		AppUser loggedInUser = appUserDao.createAppUser("tester2", "goodtests", "testuser2@example.com");
		doReturn(loggedInUser).when(appUserDao).getLoggedInAppUser();

		AppUser userToFollow1 = appUserDao.createAppUser("bob", "passpass31", "followuser@example.com");
		AppUser userToFollow2 = appUserDao.createAppUser("derek", "xcvb1sdf1", "anotheruser@example.com");

		appUserDao.followUser(userToFollow1.getUsername());
		appUserDao.followUser(userToFollow2.getUsername());

		FollowingResult followingResult = appUserDao.getFollowing(loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 2);
		assertTrue(followingResult.getFollowing().contains(userToFollow1));
		assertTrue(followingResult.getFollowing().contains(userToFollow2));
	}

	@Test
	public void followUserCaseInsensitiveTest() throws NotLoggedInException, CannotFollowYourselfException, AlreadyFollowingException, UserNotFoundException, UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		AppUser loggedInUser = appUserDao.createAppUser("tester3", "goodtests", "testuser3@example.com");
		doReturn(loggedInUser).when(appUserDao).getLoggedInAppUser();

		AppUser userToFollow = appUserDao.createAppUser("MIKE", "bcv13zxcg", "foobar@example.com");
		appUserDao.followUser(userToFollow.getUsername());
		FollowingResult followingResult = appUserDao.getFollowing(loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);
		assertTrue(followingResult.getFollowing().contains(userToFollow));
	}

	@Test
	public void unfollowUserTest() throws NotLoggedInException, CannotFollowYourselfException, AlreadyFollowingException, UserNotFoundException, UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		AppUser loggedInUser = appUserDao.createAppUser("tester4", "goodtests", "testuser4@example.com");
		doReturn(loggedInUser).when(appUserDao).getLoggedInAppUser();

		// Follow
		AppUser userToFollow = appUserDao.createAppUser("liam", "bv1xcvaw46", "booj@example.com");
		appUserDao.followUser(userToFollow.getUsername());
		FollowingResult followingResult = appUserDao.getFollowing(loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);

		// Unfollow
		appUserDao.unfollowUser(userToFollow.getUsername());
		followingResult = appUserDao.getFollowing(loggedInUser.getUsername(), 0);
		assertTrue(followingResult.getFollowing().size() == 0);
	}

	@Test
	public void unfollowUserCaseInsensitiveTest() throws NotLoggedInException, CannotFollowYourselfException, AlreadyFollowingException, UserNotFoundException, UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		AppUser loggedInUser = appUserDao.createAppUser("tester5", "goodtests", "testuser5@example.com");
		doReturn(loggedInUser).when(appUserDao).getLoggedInAppUser();

		// Follow
		AppUser userToFollow = appUserDao.createAppUser("KRING", "awetzx14sva", "kiasd@example.com");
		appUserDao.followUser(userToFollow.getUsername());
		FollowingResult followingResult = appUserDao.getFollowing(loggedInUser.getUsername(), 0);

		assertTrue(followingResult.getFollowing().size() == 1);

		// Unfollow
		appUserDao.unfollowUser(userToFollow.getUsername());
		followingResult = appUserDao.getFollowing(loggedInUser.getUsername(), 0);
		assertTrue(followingResult.getFollowing().size() == 0);
	}
}
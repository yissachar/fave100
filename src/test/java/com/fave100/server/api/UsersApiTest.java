package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.TestHelper;
import com.fave100.server.domain.UserRegistration;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.FollowingResult;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.FaveList;

public class UsersApiTest extends ApiTest {

	private AppUser loggedInUser;

	@Before
	public void setUp() {
		super.setUp();

		loggedInUser = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("loggedInPerson", "imasdf3r", "imhotep@fave100.com"));
	}

	@After
	public void tearDown() {
		super.tearDown();
		loggedInUser = null;
	}

	@Test
	public void users_api_should_find_existing_user() {
		String username = "MarkOfHearth";
		AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration(username, "gammayrays", "lasers@fave100.com"));

		assertThat(UsersApi.getAppUser(username).getUsername()).isEqualTo(username);
	}

	@Test
	public void users_api_should_find_existing_user_case_insensitive() {
		String username = "LauraOfAsgard";
		AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration(username, "thorhammer", "thenorth@fave100.com"));

		assertThat(UsersApi.getAppUser(username.toLowerCase()).getUsername()).isEqualTo(username);
	}

	@Test
	public void users_api_should_not_find_nonexisting_user() {
		try {
			UsersApi.getAppUser("SnakeOfSnakeland");
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
		}
	}

	@Test
	public void users_api_should_return_other_users_following_list() {
		AppUser userToFollow = new AppUser("asdf4");
		ofy().save().entity(userToFollow).now();

		String username = loggedInUser.getUsername();
		UserApi.followUser(loggedInUser, userToFollow.getUsername());
		loggedInUser = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("newone", "dumpasdf", "great@fave100.com"));

		FollowingResult followingResult = UsersApi.getFollowing(loggedInUser, username, 0);
		assertThat(followingResult.getFollowing().size()).isEqualTo(1);
	}

	@Test
	public void users_api_should_not_return_other_users_following_list_if_private() {
		loggedInUser.setFollowingPrivate(true);
		AppUser userToFollow = new AppUser("bc2ga");
		ofy().save().entities(userToFollow, loggedInUser).now();

		String username = loggedInUser.getUsername();
		UserApi.followUser(loggedInUser, userToFollow.getUsername());
		loggedInUser = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("byert", "14561452", "howdy@fave100.com"));

		try {
			UsersApi.getFollowing(loggedInUser, username, 0);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void users_api_should_not_return_own_following_list_even_if_private() {
		loggedInUser.setFollowingPrivate(true);
		AppUser userToFollow = new AppUser("grabba");
		ofy().save().entities(userToFollow, loggedInUser).now();

		UserApi.followUser(loggedInUser, userToFollow.getUsername());

		FollowingResult followingResult = UsersApi.getFollowing(loggedInUser, loggedInUser.getUsername(), 0);
		assertThat(followingResult.getFollowing().size()).isEqualTo(1);
	}

	@Test
	public void users_api_should_return_empty_following_list_if_not_following() {
		FollowingResult followingResult = UsersApi.getFollowing(loggedInUser, loggedInUser.getUsername(), 0);
		assertThat(followingResult.getFollowing()).isEmpty();
	}

	@Test
	public void users_api_should_get_existing_favelist() {
		String username = "someuser";
		String list = "somehash";

		FaveList faveList = new FaveList(username, list);
		faveList.getList().add(new FaveItem("the", "sheeps", "wool"));
		ofy().save().entity(faveList).now();

		assertThat(UsersApi.getFaveList(username, list).getItems().size()).isEqualTo(1);
	}

	@Test
	public void users_api_should_not_get_nonexisting_favelist() {
		try {
			UsersApi.getFaveList("something", "madeup");
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
		}
	}
}
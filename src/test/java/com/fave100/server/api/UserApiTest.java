package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import com.fave100.server.SessionAttributes;
import com.fave100.server.TestHelper;
import com.fave100.server.domain.PasswordChangeDetails;
import com.fave100.server.domain.UserRegistration;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.WhylineEdit;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.FollowingResult;
import com.fave100.server.domain.appuser.PwdResetToken;
import com.fave100.server.domain.appuser.UserInfo;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.shared.Constants;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

public class UserApiTest extends ApiTest {

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
	public void user_api_should_get_existing_logged_in_user() {
		HttpServletRequest req = TestHelper.newReq();
		String username = "one";
		AppUser loggedInUser = AuthApi.createAppUser(req, new UserRegistration(username, "dayday", "butterfly@fave100.com"));
		when(req.getSession().getAttribute(SessionAttributes.AUTH_USER)).thenReturn(username);
		assertThat(UserApi.getLoggedInUser(req)).isEqualTo(loggedInUser);
	}

	@Test
	public void user_api_should_not_get_non_existing_logged_in_user() {
		assertThat(UserApi.getLoggedInUser(TestHelper.newReq())).isNull();
	}

	@Test
	public void user_api_should_create_a_blobstore_url() {
		assertThat(UserApi.createBlobstoreUrl()).isNotNull();
	}

	@Test
	public void user_api_should_get_user_settings() {
		String email = "basd@example.com";
		AppUser user = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("foo", "barbaz", email));
		assertThat(UserApi.getCurrentUserSettings(user).getEmail()).isEqualTo(email);
	}

	@Test
	public void user_api_should_set_user_settings() {
		String newEmail = "basd@example.com";
		AppUser user = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("foo", "barbaz", "aboggabd@fave100.com"));

		UserInfo userInfo = new UserInfo();
		userInfo.setEmail(newEmail);
		UserApi.setUserInfo(user, userInfo);
		assertThat(UserApi.getCurrentUserSettings(user).getEmail()).isEqualTo(newEmail);
	}

	// TODO: test emailPasswordResetToken

	@Test
	public void user_api_should_change_password_by_passing_in_original_password() {
		String username = "rah";
		String pw = "tobechanged";
		HttpServletRequest req = TestHelper.newReq();

		AuthApi.createAppUser(req, new UserRegistration(username, pw, "lolol@fave100.com"));
		when(req.getSession().getAttribute(SessionAttributes.AUTH_USER)).thenReturn(username);

		String newPassword = "anewbetterone";
		PasswordChangeDetails changeDetails = new PasswordChangeDetails();
		changeDetails.setNewPassword(newPassword);
		changeDetails.setTokenOrPassword(pw);
		UserApi.changePassword(req, changeDetails);

		assertThat(BCrypt.checkpw(newPassword, AppUserDao.findAppUser(username).getPassword())).isTrue();
	}

	@Test
	public void user_api_should_change_password_by_passing_in_password_reset_token() {
		String username = "icthy";
		HttpServletRequest req = TestHelper.newReq();
		AuthApi.createAppUser(req, new UserRegistration(username, "cr4ck3i5", "hunmr@example.com"));
		PwdResetToken resetToken = new PwdResetToken(username);
		ofy().save().entity(resetToken).now();

		AuthApi.logout(req);

		String newPassword = "tholyigist";
		PasswordChangeDetails changeDetails = new PasswordChangeDetails(newPassword, resetToken.getToken());
		UserApi.changePassword(req, changeDetails);

		assertThat(BCrypt.checkpw(newPassword, AppUserDao.findAppUser(username).getPassword())).isTrue();
	}

	@Test
	public void user_api_should_not_change_password_by_passing_in_expired_password_reset_token() {
		String username = "thaumo";
		String oldPassword = "as!!dy3";
		HttpServletRequest req = TestHelper.newReq();

		AuthApi.createAppUser(req, new UserRegistration(username, oldPassword, "hasdg@example.com"));
		PwdResetToken resetToken = new PwdResetToken(username);
		resetToken.setExpiry(new Date(new Date().getTime() - PwdResetToken.EXPIRY_TIME - 1));
		ofy().save().entity(resetToken).now();

		AuthApi.logout(req);
		String newPassword = "greatlify";
		PasswordChangeDetails changeDetails = new PasswordChangeDetails(newPassword, resetToken.getToken());
		UserApi.changePassword(req, changeDetails);

		assertThat(BCrypt.checkpw(oldPassword, AppUserDao.findAppUser(username).getPassword())).isTrue();
	}

	@Test
	public void user_api_should_create_favelist() throws Exception {
		String faveListName = "anewfavelist";
		UserApi.addFaveListForCurrentUser(loggedInUser, faveListName);

		assertThat(FaveListDao.findFaveList(loggedInUser.getUsername(), faveListName)).isNotNull();
	}

	@Test
	public void user_api_should_not_create_favelist_with_duplicate_name_per_user() {
		String faveListName = "boo";
		UserApi.addFaveListForCurrentUser(loggedInUser, faveListName);

		// Attempt to add a second favelist with same name
		try {
			UserApi.addFaveListForCurrentUser(loggedInUser, faveListName);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void user_api_should_not_create_favelist_with_null_name() {
		try {
			UserApi.addFaveListForCurrentUser(loggedInUser, null);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void user_api_should_not_create_favelist_with_empty_name() {
		try {
			UserApi.addFaveListForCurrentUser(loggedInUser, "");
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void user_api_should_not_create_favelist_with_name_that_is_too_long() {
		// Create a favelist with a name that is too long
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Constants.MAX_HASHTAG_LENGTH + 1; i++) {
			sb.append('a');
		}

		String faveListName = sb.toString();

		try {
			UserApi.addFaveListForCurrentUser(loggedInUser, faveListName);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void user_api_should_not_create_favelist_with_non_alphanumeric_name() {
		String name1 = "foo%";
		String name2 = "^bar";
		String name3 = "baz:";
		String name4 = "baz inga";

		try {
			UserApi.addFaveListForCurrentUser(loggedInUser, name1);
			UserApi.addFaveListForCurrentUser(loggedInUser, name2);
			UserApi.addFaveListForCurrentUser(loggedInUser, name3);
			UserApi.addFaveListForCurrentUser(loggedInUser, name4);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void user_api_should_not_create_favelist_if_list_limit_reached() {
		Set<String> faveListNames = new HashSet<>();

		// Create 99 new favelists (user already has 1 list, alltime)
		for (int i = 0; i < Constants.MAX_LISTS_PER_USER - 1; i++) {
			faveListNames.add(String.valueOf(i));
		}

		// Cheat by simply setting the user's hashtags instead of actually saving each FaveList into datastore
		loggedInUser.setHashtags(new ArrayList<>(faveListNames));

		String faveListName = "101stlist";

		try {
			UserApi.addFaveListForCurrentUser(loggedInUser, faveListName);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}

		assertThat(FaveListDao.findFaveList(loggedInUser.getUsername(), faveListName)).isNull();
	}

	@Test
	public void user_api_should_create_favelist_that_is_empty() {
		String faveListName = "booya";
		UserApi.addFaveListForCurrentUser(loggedInUser, faveListName);

		FaveList faveList = FaveListDao.findFaveList(loggedInUser.getUsername(), faveListName);
		assertThat(faveList.getList()).isEmpty();
	}

	/**
	 * When a FaveList is created, the name of the list is also stored in the User object. We
	 * denormalize this for better performance so as not to have to perform N FaveList fetches
	 * to retrieve a list of FaveList names for a user.
	 */
	@Test
	public void user_api_should_store_favelist_name_in_user_profile() {
		String listName = "gretza";
		UserApi.addFaveListForCurrentUser(loggedInUser, listName);

		assertThat(loggedInUser.getHashtags()).contains(listName);
	}

	@Test
	public void user_api_should_delete_favelist() {
		String faveListName = "favelisttodelete";
		UserApi.addFaveListForCurrentUser(loggedInUser, faveListName);

		UserApi.deleteFaveListForCurrentUser(loggedInUser, faveListName);

		assertThat(FaveListDao.findFaveList(loggedInUser.getUsername(), faveListName)).isNull();
	}

	@Test
	public void user_api_should_delete_favelist_associated_whylines() {
		String faveListName = "flwithwhylines";
		String songId = "kEMkxg";
		UserApi.addFaveListForCurrentUser(loggedInUser, faveListName);
		UserApi.addFaveItemForCurrentUser(loggedInUser, faveListName, songId);

		WhylineEdit whylineEdit = new WhylineEdit(faveListName, songId, "hah this is great");
		UserApi.editWhylineForCurrentUser(loggedInUser, whylineEdit);
		UserApi.deleteFaveListForCurrentUser(loggedInUser, faveListName);

		assertThat(SongApi.getWhylines(songId).getItems()).isEmpty();
	}

	@Test
	public void user_api_should_add_fave_item_with_proper_id() {
		UserApi.addFaveItemForCurrentUser(loggedInUser, Constants.DEFAULT_HASHTAG, "kEMkxg");
		assertThat(UsersApi.getFaveList(loggedInUser.getUsername(), Constants.DEFAULT_HASHTAG).getItems().size()).isEqualTo(1);
	}

	@Test
	public void user_api_should_not_add_fave_item_with_invalid_id() {
		try {
			UserApi.addFaveItemForCurrentUser(loggedInUser, Constants.DEFAULT_HASHTAG, "111111111111111");
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
		}
	}

	@Test
	public void user_api_should_not_add_fave_item_if_list_size_limit_reached() {
		FaveList faveList = FaveListDao.findFaveList(loggedInUser.getUsername(), Constants.DEFAULT_HASHTAG);
		for (int i = 0; i < Constants.MAX_ITEMS_PER_LIST; i++) {
			String iString = String.valueOf(i);
			FaveItem faveItem = new FaveItem(iString, iString, iString);
			faveList.getList().add(faveItem);
		}
		ofy().save().entity(faveList).now();

		try {
			UserApi.addFaveItemForCurrentUser(loggedInUser, Constants.DEFAULT_HASHTAG, "kEMkxg");
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void user_api_should_not_add_fave_item_if_list_already_contains_that_fave_item() {
		String songId = "kEMkxg";
		TestHelper.addSingleFaveItemToDefaultList(loggedInUser, songId);

		try {
			UserApi.addFaveItemForCurrentUser(loggedInUser, Constants.DEFAULT_HASHTAG, songId);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void user_api_should_remove_existing_fave_item_from_existing_fave_list() {
		String songId = "kEMkxg";
		TestHelper.addSingleFaveItemToDefaultList(loggedInUser, songId);

		UserApi.removeFaveItemForCurrentUser(loggedInUser, Constants.DEFAULT_HASHTAG, songId);
		assertThat(UsersApi.getFaveList(loggedInUser.getUsername(), Constants.DEFAULT_HASHTAG).getItems()).isEmpty();
	}

	@Test
	public void user_api_should_remove_existing_fave_item_associated_whyline() {
		String songId = "kEMkxg";

		Whyline whyline = new Whyline("Blah goo", songId, loggedInUser.getUsername(), Constants.DEFAULT_HASHTAG);
		ofy().save().entity(whyline).now();

		FaveList faveList = FaveListDao.findFaveList(loggedInUser.getUsername(), Constants.DEFAULT_HASHTAG);
		FaveItem faveItem = new FaveItem("123af", "7fdj2", songId);
		faveItem.setWhyline("Blag hoo");
		faveItem.setWhylineRef(Ref.create(Key.create(Whyline.class, whyline.getId())));
		faveList.getList().add(faveItem);
		ofy().save().entity(faveList).now();

		UserApi.removeFaveItemForCurrentUser(loggedInUser, Constants.DEFAULT_HASHTAG, songId);

		assertThat(ofy().load().type(Whyline.class).id(whyline.getId()).now()).isNull();
	}

	@Test
	public void user_api_should_rerank_existing_fave_item() {
		String songId1 = "BARG";
		String songId2 = "asdf";

		TestHelper.addSingleFaveItemToDefaultList(loggedInUser, songId1);
		TestHelper.addSingleFaveItemToDefaultList(loggedInUser, songId2);

		UserApi.rerankFaveItemForCurrentUser(loggedInUser, Constants.DEFAULT_HASHTAG, songId1, 1);
		FaveItem firstFaveItem = UsersApi.getFaveList(loggedInUser.getUsername(), Constants.DEFAULT_HASHTAG).getItems().get(1);
		assertThat(firstFaveItem.getId()).isEqualTo(songId1);
	}

	@Test
	public void user_api_should_not_rerank_fave_item_out_of_bounds() {
		String songId = "ladonna";
		TestHelper.addSingleFaveItemToDefaultList(loggedInUser, songId);

		try {
			UserApi.rerankFaveItemForCurrentUser(loggedInUser, Constants.DEFAULT_HASHTAG, songId, 3);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void user_api_should_add_new_whyline_to_fave_item() {
		String songId = "mabelle";
		TestHelper.addSingleFaveItemToDefaultList(loggedInUser, songId);

		String whyline = "Howdy bud!";
		WhylineEdit whylineEdit = new WhylineEdit(Constants.DEFAULT_HASHTAG, songId, whyline);
		UserApi.editWhylineForCurrentUser(loggedInUser, whylineEdit);

		assertThat(UsersApi.getFaveList(loggedInUser.getUsername(), Constants.DEFAULT_HASHTAG).getItems()).extracting("whyline").contains(whyline);
		assertThat(ofy().load().type(Whyline.class).id(1).now().getWhyline()).isEqualTo(whyline);
	}

	@Test
	public void user_api_should_edit_existing_whyline_for_fave_item() {
		String songId = "whodunit";
		TestHelper.addSingleFaveItemToDefaultList(loggedInUser, songId);

		String oldWhyline = "This be a old whyline";
		WhylineEdit whylineEdit = new WhylineEdit(Constants.DEFAULT_HASHTAG, songId, oldWhyline);
		UserApi.editWhylineForCurrentUser(loggedInUser, whylineEdit);

		String newWhyline = "and this a new";
		WhylineEdit whylineEdit2 = new WhylineEdit(Constants.DEFAULT_HASHTAG, songId, newWhyline);
		UserApi.editWhylineForCurrentUser(loggedInUser, whylineEdit2);

		assertThat(UsersApi.getFaveList(loggedInUser.getUsername(), Constants.DEFAULT_HASHTAG).getItems()).extracting("whyline").contains(newWhyline);
		assertThat(ofy().load().type(Whyline.class).id(1).now().getWhyline()).isEqualTo(newWhyline);
	}

	@Test
	public void user_api_should_follow_existing_user() {
		AppUser userToFollow = new AppUser("followme");
		ofy().save().entity(userToFollow).now();

		UserApi.followUser(loggedInUser, userToFollow.getUsername());

		FollowingResult followingResult = UsersApi.getFollowing(loggedInUser, loggedInUser.getUsername(), 0);

		assertThat(followingResult.getFollowing().size()).isEqualTo(1);
		assertThat(followingResult.getFollowing()).contains(userToFollow);
	}

	@Test
	public void users_api_should_follow_multiple_users() {
		AppUser userToFollow1 = new AppUser("afirsty");
		AppUser userToFollow2 = new AppUser("asecondy");
		ofy().save().entities(userToFollow1, userToFollow2).now();

		UserApi.followUser(loggedInUser, userToFollow1.getUsername());
		UserApi.followUser(loggedInUser, userToFollow2.getUsername());

		FollowingResult followingResult = UsersApi.getFollowing(loggedInUser, loggedInUser.getUsername(), 0);

		assertThat(followingResult.getFollowing().size()).isEqualTo(2);
		assertThat(followingResult.getFollowing()).contains(userToFollow1);
		assertThat(followingResult.getFollowing()).contains(userToFollow2);
	}

	@Test
	public void users_api_should_follow_user_regardless_of_name_case() {
		AppUser userToFollow = new AppUser("KRING");
		ofy().save().entity(userToFollow).now();

		UserApi.followUser(loggedInUser, userToFollow.getUsername());
		FollowingResult followingResult = UsersApi.getFollowing(loggedInUser, loggedInUser.getUsername(), 0);

		assertThat(followingResult.getFollowing().size()).isEqualTo(1);
		assertThat(followingResult.getFollowing()).contains(userToFollow);
	}

	@Test
	public void users_api_should_unfollow_followed_user() {
		AppUser userToFollow = new AppUser("baddo");
		ofy().save().entity(userToFollow).now();

		UserApi.followUser(loggedInUser, userToFollow.getUsername());
		UserApi.unfollowUser(loggedInUser, userToFollow.getUsername());

		FollowingResult followingResult = UsersApi.getFollowing(loggedInUser, loggedInUser.getUsername(), 0);

		assertThat(followingResult.getFollowing()).isEmpty();
	}

	@Test
	public void should_unfollow_followed_user_regardless_of_name_case() {
		AppUser userToFollow = new AppUser("jotheman");
		ofy().save().entity(userToFollow).now();

		UserApi.followUser(loggedInUser, userToFollow.getUsername());
		UserApi.unfollowUser(loggedInUser, userToFollow.getUsername());

		FollowingResult followingResult = UsersApi.getFollowing(loggedInUser, loggedInUser.getUsername(), 0);
		assertThat(followingResult.getFollowing()).isEmpty();
	}

	@Test
	public void users_api_should_not_allow_user_to_follow_themselves() {
		try {
			UserApi.followUser(loggedInUser, loggedInUser.getUsername());
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void users_api_should_not_allow_user_to_follow_a_user_they_are_already_following() {
		AppUser userToFollow = new AppUser("nottwice");
		ofy().save().entity(userToFollow).now();

		UserApi.followUser(userToFollow, loggedInUser.getUsername());

		try {
			UserApi.followUser(userToFollow, loggedInUser.getUsername());
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}
}
package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.fave100.server.TestHelper;
import com.fave100.server.domain.UserRegistration;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.LoginCredentials;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.shared.Constants;

public class AuthApiTest extends ApiTest {

	@Test
	public void auth_api_should_register_user_natively() {
		assertEquals("There must not be any pre-existing users", 0, ofy().load().type(AppUser.class).count());

		String username = "test";
		AppUser user = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration(username, "123456", "blah@foobar.com"));

		assertNotNull("Created user cannot be null", user);
		assertEquals("Created user must exist in datastore", user, AppUserDao.findAppUser(username));
	}

	// TODO: Test third party users here

	@Test
	public void auth_api_should_not_register_native_user_with_empty_username() {
		try {
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("", "security", "testuser@example.com"));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void auth_api_should_not_register_native_user_with_username_that_is_too_long() {
		String username = "";
		for (int i = 0; i < Constants.MAX_USERNAME_LENGTH + 1; i++) {
			username += "a";
		}

		try {
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration(username, "apassword", "testuser@example.com"));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void auth_api_should_not_register_user_with_non_alphanumeric_username() {
		try {
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("bob&", "apassword", "testuser1@example.com"));
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("-", "apassword", "testuser2@example.com"));
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("jo hn", "apassword", "testuser3@example.com"));
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("@me", "apassword", "testuser4@example.com"));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void auth_api_should_not_register_user_with_duplicate_username() {
		String username = "bob";
		AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration(username, "123456", "testuser1@example.com"));

		try {
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration(username, "654321", "testuser2@example.com"));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void auth_api_should_not_register_native_user_with_empty_password() {
		try {
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("lax", "", "testuser@example.com"));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void auth_api_should_not_store_password_in_plain_text() {
		String username = "larry";
		String pw = "superSecurePw";

		AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration(username, pw, "testuser@example.com"));

		String storedPw = AppUserDao.findAppUser(username).getPassword();
		assertFalse("The stored password should not be equal to the plaintext submitted password", pw.equals(storedPw));
	}

	@Test
	public void auth_api_should_register_user_even_with_duplicate_password() {
		String pw = "test@example.coms";

		AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("bob", pw, "test1@example.com"));

		AppUser secondAppUser = null;
		secondAppUser = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("alex", pw, "test2@example.com"));

		assertNotNull(secondAppUser);
	}

	@Test
	public void auth_api_should_not_register_user_with_duplicate_email_address() {
		String email = "test@example.com";
		AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("bob", "123456", email));

		try {
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("mike", "654321", email));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void auth_api_should_create_empty_favelist_for_created_user() {
		String username = "Alfred";
		AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration(username, "butterworthy", "asdf@example.com"));
		assertNotNull(FaveListDao.findFaveList(username, Constants.DEFAULT_HASHTAG));

	}

	@Test
	public void auth_api_should_login_with_username_and_password() {
		final String username = "joeyfigi";
		final String pw = "chandlerasd";

		AppUser createdAppUser = TestHelper.createLoggedOutUser(username, pw, "tankman@example.com");
		AppUser loggedInAppUser = AuthApi.login(TestHelper.newReq(), new LoginCredentials(username, pw));

		assertEquals(createdAppUser, loggedInAppUser);
	}

	@Test
	public void auth_api_should_login_with_email_and_password() {
		final String pw = "Hodor!Hodor!";
		final String email = "letmein@gmail.com";

		AppUser createdAppUser = TestHelper.createLoggedOutUser("Thrain", pw, email);
		AppUser loggedInAppUser = AuthApi.login(TestHelper.newReq(), new LoginCredentials(email, pw));

		assertEquals(createdAppUser, loggedInAppUser);
	}

	@Test
	public void auth_api_should_not_login_with_empty_username() {
		String pw = "yetiscoming5u";
		TestHelper.createLoggedOutUser("Geraldo", pw, "worldeater@fave100.com");

		try {
			AuthApi.login(TestHelper.newReq(), new LoginCredentials("", pw));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
		}
	}

	@Test
	public void auth_api_should_not_login_with_incorrect_username() {
		String pw = "40p3er";
		TestHelper.createLoggedOutUser("mikey", pw, "goo@bargoo.com");

		try {
			AuthApi.login(TestHelper.newReq(), new LoginCredentials("notreallyyou", pw));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
		}
	}

	@Test
	public void auth_api_should_not_login_with_empty_password() {
		String username = "someone";
		TestHelper.createLoggedOutUser(username, "once3Eonce", "bbqtime@almost.com");

		try {
			AuthApi.login(TestHelper.newReq(), new LoginCredentials(username, ""));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
		}
	}

	@Test
	public void auth_api_should_not_login_with_incorrect_password() {
		String username = "thegreywizard";
		TestHelper.createLoggedOutUser(username, "juperPa5s", "bar@barbar.com");

		try {
			AuthApi.login(TestHelper.newReq(), new LoginCredentials(username, "FailPass"));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
		}
	}

	// TODO: Test third party logins

	@Test
	public void auth_api_should_logout_logged_in_user() {
		HttpServletRequest req = TestHelper.newReq();
		AuthApi.createAppUser(req, new UserRegistration("GrandMaester", "toppAaat", "haberdashier@fave100.com"));

		AuthApi.logout(req);

		assertNull("User must be logged out", UserApi.getLoggedInUser(req));
	}

}
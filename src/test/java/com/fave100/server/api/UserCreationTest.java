package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.TestHelper;
import com.fave100.server.domain.UserRegistration;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.Constants;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;

public class UserCreationTest {

	static {
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(FaveList.class);
		ObjectifyService.register(EmailID.class);
	}

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		ObjectifyFilter.complete();
		helper.tearDown();
	}

	@Test
	public void should_register_user_natively() {
		// No users exist in datastore
		assertEquals("There must not be pre-existing users", 0, ofy().load().type(AppUser.class).count());

		String username = "test";
		String pw = "123456";

		AppUser appUser = null;

		// Create a user
		UserRegistration registration = new UserRegistration();
		registration.setUsername(username);
		registration.setPassword(pw);
		registration.setEmail("blah@foobar.com");

		appUser = AuthApi.createAppUser(TestHelper.newReq(), registration);

		// User now exists in datastore
		assertNotNull("Created user cannot be null", appUser);
		assertEquals("Created user must exist in datastore", appUser, AppUserDao.findAppUser(username));
	}

	// TODO: Test third party users here

	@Test
	public void should_not_register_user_with_empty_username() {
		UserRegistration registration = new UserRegistration();
		registration.setUsername("");
		registration.setPassword("security");
		registration.setEmail("testuser@example.com");

		try {
			AuthApi.createAppUser(TestHelper.newReq(), registration);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void should_not_register_user_with_username_that_is_too_long() {

		String username = "";
		for (int i = 0; i < Constants.MAX_USERNAME_LENGTH + 1; i++) {
			username += "a";
		}

		UserRegistration registration = new UserRegistration();
		registration.setUsername(username);
		registration.setPassword("apassword");
		registration.setEmail("testuser@example.com");

		try {
			AuthApi.createAppUser(TestHelper.newReq(), registration);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void should_not_register_user_with_non_alphanumeric_username() {
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
	public void should_not_register_user_with_duplicate_username() {
		String username = "bob";

		// Create a user with that name		
		AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration(username, "123456", "testuser1@example.com"));

		// Try creating user with same name
		try {
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration(username, "654321", "testuser2@example.com"));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void should_not_register_user_with_empty_password() {
		try {
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("lax", "", "testuser@example.com"));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void should_not_store_password_in_plain_text() {
		String username = "larry";
		String pw = "superSecurePw";

		AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration(username, pw, "testuser@example.com"));

		String storedPw = AppUserDao.findAppUser(username).getPassword();
		assertFalse("Password must not be stored in plaintext", pw.equals(storedPw));
	}

	@Test
	public void should_register_user_with_duplicate_password() {
		String pw = "test@example.coms";

		AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("bob", pw, "test1@example.com"));

		AppUser secondAppUser = null;
		secondAppUser = AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("alex", pw, "test2@example.com"));

		assertNotNull("User with duplicate passwoed should be created", secondAppUser);
	}

	@Test
	public void should_not_register_email_with_duplicate_email() {
		String email = "test@example.coms";

		// Create a user with	
		AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("bob", "123456", email));

		// Try creating a user with the same email
		try {
			AuthApi.createAppUser(TestHelper.newReq(), new UserRegistration("mike", "654321", email));
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.FORBIDDEN.getStatusCode());
		}
	}
}
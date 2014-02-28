package com.fave100.server;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.api.UsersApi;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.Constants;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.google.api.server.spi.response.BadRequestException;
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
	private AppUserDao appUserDao = null;
	private UsersApi appUserApi;

	@Before
	public void setUp() {
		helper.setUp();
		appUserDao = new AppUserDao();
		appUserApi = new UsersApi(appUserDao);
	}

	@After
	public void tearDown() {
		ObjectifyFilter.complete();
		helper.tearDown();
	}

	@Test
	public void nativeUserCanBeCreated() throws BadRequestException, EmailIDAlreadyExistsException {
		// No users exist in datastore
		assertEquals("There must not be pre-existing users", 0, ofy().load().type(AppUser.class).count());

		String username = "test";
		String pw = "123456";

		AppUser appUser = null;

		// Create a user
		appUser = appUserApi.createAppUser(TestHelper.newReq(), username, pw, "testuser@example.com").getAppUser();

		// User now exists in datastore
		assertNotNull("Created user cannot be null", appUser);
		assertEquals("Created user must exist in datastore", appUser, appUserDao.findAppUser(username));
	}

	// TODO: Test third party users here

	@Test
	public void userNameMustNotBeEmpty() throws BadRequestException {
		AppUser appUser = null;

		try {
			appUser = appUserApi.createAppUser(TestHelper.newReq(), "", "security", "testuser@example.com").getAppUser();
			fail("Exception not thrown");
		}
		catch (BadRequestException e) {
			// Success!
		}

		assertNull("User must not be created with empty user name", appUser);
	}

	@Test
	public void userNameMustNotBeTooLong() throws BadRequestException {
		AppUser appUser = null;

		String username = "";
		for (int i = 0; i < Constants.MAX_USERNAME_LENGTH + 1; i++) {
			username += "a";
		}
		appUser = appUserApi.createAppUser(TestHelper.newReq(), username, "apassword", "testuser@example.com").getAppUser();

		assertNull("User must not be created if name is too long", appUser);
	}

	@Test
	public void userNameMustOnlyContainLettersAndNumbers() throws BadRequestException {
		AppUser appUser1 = null;
		AppUser appUser2 = null;
		AppUser appUser3 = null;
		AppUser appUser4 = null;

		appUser1 = appUserApi.createAppUser(TestHelper.newReq(), "bob&", "apassword", "testuser1@example.com").getAppUser();
		appUser2 = appUserApi.createAppUser(TestHelper.newReq(), "-", "apassword", "testuser2@example.com").getAppUser();
		appUser3 = appUserApi.createAppUser(TestHelper.newReq(), "jo hn", "apassword", "testuser3@example.com").getAppUser();
		appUser4 = appUserApi.createAppUser(TestHelper.newReq(), "@me", "apassword", "testuser4@example.com").getAppUser();

		String msg = "User must not be created if name does not only contain letters and numbes";
		assertNull(msg, appUser1);
		assertNull(msg, appUser2);
		assertNull(msg, appUser3);
		assertNull(msg, appUser4);
	}

	@Test
	public void userNameMustBeUnique() throws BadRequestException {
		String username = "bob";

		// Create a user with that name		
		appUserApi.createAppUser(TestHelper.newReq(), username, "123456", "testuser1@example.com");

		// User with duplicate username not created
		AppUser secondAppUser = null;
		try {
			secondAppUser = appUserApi.createAppUser(TestHelper.newReq(), username, "654321", "testuser2@example.com").getAppUser();
			fail("Exception not thrown");
		}
		catch (BadRequestException e) {
			// Success!
		}

		assertNull("User with duplicate username must not be created", secondAppUser);
	}

	@Test
	public void passwordMustNotBeEmpty() throws BadRequestException {
		AppUser appUser = null;
		appUser = appUserApi.createAppUser(TestHelper.newReq(), "lax", "", "testuser@example.com").getAppUser();

		assertNull("User must not be created with empty password", appUser);
	}

	@Test
	public void passwordMustNotBeStoredInPlaintext() throws BadRequestException {

		String username = "larry";
		String pw = "superSecurePw";

		appUserApi.createAppUser(TestHelper.newReq(), username, pw, "testuser@example.com");

		String storedPw = appUserDao.findAppUser(username).getPassword();
		assertFalse("Password must not be stored in plaintext", pw.equals(storedPw));
	}

	@Test
	public void passwordNeedNotBeUnique() throws BadRequestException {
		String pw = "test@example.coms";

		appUserApi.createAppUser(TestHelper.newReq(), "bob", pw, "test1@example.com");

		AppUser secondAppUser = null;
		secondAppUser = appUserApi.createAppUser(TestHelper.newReq(), "alex", pw, "test2@example.com").getAppUser();

		assertNotNull("User with duplicate passwoed should be created", secondAppUser);
	}

	@Test
	public void userEmailMustBeUnique() throws BadRequestException {
		String email = "test@example.coms";

		// Create a user with the same email		
		appUserApi.createAppUser(TestHelper.newReq(), "bob", "123456", email);

		// User with duplicate username not created
		AppUser secondAppUser = null;
		try {
			secondAppUser = appUserApi.createAppUser(TestHelper.newReq(), "mike", "654321", email).getAppUser();
			fail("Exception not thrown");
		}
		catch (BadRequestException e) {
			// Success!!
		}

		assertNull("User with duplicate email must not be created", secondAppUser);
	}
}
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

import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.Constants;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.fave100.shared.exceptions.user.UsernameAlreadyExistsException;
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

	@Before
	public void setUp() {
		helper.setUp();
		appUserDao = new AppUserDao();
	}

	@After
	public void tearDown() {
		ObjectifyFilter.complete();
		helper.tearDown();
	}

	@Test
	public void nativeUserCanBeCreated() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		// No users exist in datastore
		assertEquals("There must not be pre-existing users", 0, ofy().load().type(AppUser.class).count());

		String username = "test";
		String pw = "123456";

		AppUser appUser = null;

		// Create a user
		appUser = appUserDao.createAppUser(username, pw, "testuser@example.com");

		// User now exists in datastore
		assertNotNull("Created user cannot be null", appUser);
		assertEquals("Created user must exist in datastore", appUser, appUserDao.findAppUser(username));
	}

	// TODO: Test third party users here

	@Test
	public void userNameMustNotBeEmpty() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		AppUser appUser = null;

		appUser = appUserDao.createAppUser("", "security", "testuser@example.com");

		assertNull("User must not be created with empty user name", appUser);
	}

	@Test
	public void userNameMustNotBeTooLong() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		AppUser appUser = null;

		String username = "";
		for (int i = 0; i < Constants.MAX_USERNAME_LENGTH + 1; i++) {
			username += "a";
		}
		appUser = appUserDao.createAppUser(username, "apassword", "testuser@example.com");

		assertNull("User must not be created if name is too long", appUser);
	}

	@Test
	public void userNameMustOnlyContainLettersAndNumbers() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		AppUser appUser1 = null;
		AppUser appUser2 = null;
		AppUser appUser3 = null;
		AppUser appUser4 = null;

		appUser1 = appUserDao.createAppUser("bob&", "apassword", "testuser1@example.com");
		appUser2 = appUserDao.createAppUser("-", "apassword", "testuser2@example.com");
		appUser3 = appUserDao.createAppUser("jo hn", "apassword", "testuser3@example.com");
		appUser4 = appUserDao.createAppUser("@me", "apassword", "testuser4@example.com");

		String msg = "User must not be created if name does not only contain letters and numbes";
		assertNull(msg, appUser1);
		assertNull(msg, appUser2);
		assertNull(msg, appUser3);
		assertNull(msg, appUser4);
	}

	@Test
	public void userNameMustBeUnique() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		String username = "bob";

		// Create a user with that name		
		appUserDao.createAppUser(username, "123456", "testuser1@example.com");

		// User with duplicate username not created
		AppUser secondAppUser = null;
		try {
			secondAppUser = appUserDao.createAppUser(username, "654321", "testuser2@example.com");
			fail("Exception not thrown");
		}
		catch (UsernameAlreadyExistsException | EmailIDAlreadyExistsException e) {
			// Success!
		}

		assertNull("User with duplicate username must not be created", secondAppUser);
	}

	@Test
	public void passwordMustNotBeEmpty() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		AppUser appUser = null;
		appUser = appUserDao.createAppUser("lax", "", "testuser@example.com");

		assertNull("User must not be created with empty password", appUser);
	}

	@Test
	public void passwordMustNotBeStoredInPlaintext() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException {

		String username = "larry";
		String pw = "superSecurePw";

		appUserDao.createAppUser(username, pw, "testuser@example.com");

		String storedPw = appUserDao.findAppUser(username).getPassword();
		assertFalse("Password must not be stored in plaintext", pw.equals(storedPw));
	}

	@Test
	public void passwordNeedNotBeUnique() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		String pw = "test@example.coms";

		appUserDao.createAppUser("bob", pw, "test1@example.com");

		AppUser secondAppUser = null;
		secondAppUser = appUserDao.createAppUser("alex", pw, "test2@example.com");

		assertNotNull("User with duplicate passwoed should be created", secondAppUser);
	}

	@Test
	public void userEmailMustBeUnique() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		String email = "test@example.coms";

		// Create a user with the same email		
		appUserDao.createAppUser("bob", "123456", email);

		// User with duplicate username not created
		AppUser secondAppUser = null;
		try {
			secondAppUser = appUserDao.createAppUser("mike", "654321", email);
			fail("Exception not thrown");
		}
		catch (UsernameAlreadyExistsException | EmailIDAlreadyExistsException e) {
			// Success!!
		}

		assertNull("User with duplicate email must not be created", secondAppUser);
	}
}
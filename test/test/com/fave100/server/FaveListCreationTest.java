package test.com.fave100.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.server.domain.favelist.Hashtag;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;

public class FaveListCreationTest {

	static {
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(FaveList.class);
		ObjectifyService.register(EmailID.class);
		ObjectifyService.register(Hashtag.class);
	}

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void faveListCreated() throws Exception {
		// Create a user
		String username = "tester";
		AppUserDao appUserDao = new AppUserDao();
		AppUser appUser = appUserDao.createAppUser(username, "goodtests", "testuser@example.com");

		AppUserDao mockAppUserDao = mock(AppUserDao.class);
		when(mockAppUserDao.getLoggedInAppUser()).thenReturn(appUser);

		// Create a favelist
		String faveListName = "anewfavelist";
		FaveListDao faveListDao = new FaveListDao(mockAppUserDao);
		faveListDao.addFaveListForCurrentUser(faveListName);

		// Favelist now exists in datastore
		assertNotNull("Created faveList must exist in datastore", faveListDao.findFaveList(username, faveListName));
	}

	@Test
	public void faveListMustBeUniquePerUser() throws Exception {
		// Create a user
		String username = "tester";
		AppUserDao appUserDao = new AppUserDao();
		AppUser appUser = appUserDao.createAppUser(username, "goodtests", "testuser@example.com");

		AppUserDao mockAppUserDao = mock(AppUserDao.class);
		when(mockAppUserDao.getLoggedInAppUser()).thenReturn(appUser);

		// Create a favelist
		String faveListName = "boo";
		FaveListDao faveListDao = new FaveListDao(mockAppUserDao);
		faveListDao.addFaveListForCurrentUser(faveListName);

		// Attempt to add a second favelist
		try {
			faveListDao.addFaveListForCurrentUser(faveListName);
			fail("Exception not thrown");
		}
		catch (Exception e) {
			// Success!
		}
	}
}
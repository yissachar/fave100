package test.com.fave100.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.shared.Constants;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.fave100.shared.exceptions.user.UsernameAlreadyExistsException;
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
	private AppUser loggedInUser;
	private FaveListDao faveListDao;

	@Before
	public void setUp() throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		helper.setUp();
		// Create a user
		String username = "tester";
		AppUserDao appUserDao = new AppUserDao();
		loggedInUser = appUserDao.createAppUser(username, "goodtests", "testuser@example.com");

		AppUserDao mockAppUserDao = mock(AppUserDao.class);
		when(mockAppUserDao.getLoggedInAppUser()).thenReturn(loggedInUser);

		faveListDao = new FaveListDao(mockAppUserDao);
	}

	@After
	public void tearDown() {
		helper.tearDown();
		loggedInUser = null;
		faveListDao = null;
	}

	@Test
	public void faveListCreated() throws Exception {
		// Create a favelist
		String faveListName = "anewfavelist";
		faveListDao.addFaveListForCurrentUser(faveListName);

		// Favelist now exists in datastore
		assertNotNull("Created faveList must exist in datastore", faveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}

	@Test
	public void faveListNameMustBeUniquePerUser() throws Exception {
		// Create a favelist
		String faveListName = "boo";
		faveListDao.addFaveListForCurrentUser(faveListName);

		// Attempt to add a second favelist with same name
		try {
			faveListDao.addFaveListForCurrentUser(faveListName);
			fail("Exception not thrown");
		}
		catch (Exception e) {
			// Success!
		}
	}

	@Test
	public void faveListNameMustNotBeNull() throws Exception {
		// Create a favelist with null name
		String faveListName = null;

		try {
			faveListDao.addFaveListForCurrentUser(faveListName);
			fail("Exception not thrown");
		}
		catch (Exception e) {
			// Success!
		}
	}

	@Test
	public void faveListNameMustNotBeEmpty() throws Exception {
		// Create a favelist with empty name
		String faveListName = "";

		try {
			faveListDao.addFaveListForCurrentUser(faveListName);
			fail("Exception not thrown");
		}
		catch (Exception e) {
			// Success!
		}

		assertNull("FaveList with empty name must not be created", faveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}

	@Test
	public void faveListNameMustNotBeTooLong() throws Exception {
		// Create a favelist with a name that is too long
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Constants.MAX_HASHTAG_LENGTH + 1; i++) {
			sb.append("a");
		}

		String faveListName = sb.toString();

		try {
			faveListDao.addFaveListForCurrentUser(faveListName);
			fail("Exception not thrown");
		}
		catch (Exception e) {
			// Success!
		}

		assertNull("FaveList with a name that is too long must not be created", faveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}

	@Test
	public void faveListNameMustOnlyConsistOfLettersAndNumbers() throws Exception {
		// Create favelists with special chars and spaces
		String name1 = "foo%";
		String name2 = "^bar";
		String name3 = "baz:";
		String name4 = "baz inga";

		try {
			faveListDao.addFaveListForCurrentUser(name1);
			faveListDao.addFaveListForCurrentUser(name2);
			faveListDao.addFaveListForCurrentUser(name3);
			faveListDao.addFaveListForCurrentUser(name4);
			fail("Exception not thrown");
		}
		catch (Exception e) {
			// Success!
		}

		String msg = "FaveList with a name that is too long must not be created";
		assertNull(msg, faveListDao.findFaveList(loggedInUser.getUsername(), name1));
		assertNull(msg, faveListDao.findFaveList(loggedInUser.getUsername(), name2));
		assertNull(msg, faveListDao.findFaveList(loggedInUser.getUsername(), name3));
		assertNull(msg, faveListDao.findFaveList(loggedInUser.getUsername(), name4));
	}

	@Test
	public void mustNotCreateTooManyFaveLists() throws Exception {
		// Alphabet for naming lists
		char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z'};
		// Store used faveListNames to avoid creating list with duplicate name
		Set<String> faveListNames = new HashSet<>();

		// Create 99 randomly named favelists (user already has 1 list, alltime)
		for (int i = 0; i < Constants.MAX_LISTS_PER_USER - 1; i++) {
			StringBuilder sb = new StringBuilder();
			while (sb.toString().isEmpty() || faveListNames.contains(sb.toString())) {
				sb = new StringBuilder();
				// Dividing the length by 3 is just for performance speed
				for (int j = 0; j < Constants.MAX_HASHTAG_LENGTH / 3; j++) {
					sb.append(alphabet[((int)(Math.random() * (alphabet.length - 1)))]);
				}
			}
			faveListNames.add(sb.toString());
		}

		// Cheat by simply setting the user's hashtags instead of actually saving each FaveList into datastore
		loggedInUser.setHashtags(new ArrayList<>(faveListNames));

		// Guaranteed not to be in lists already, since numbers were not part of the alphabet
		String faveListName = "101stlist";

		try {
			faveListDao.addFaveListForCurrentUser(faveListName);
			fail("Exception not thrown");
		}
		catch (Exception e) {
			// Success!
		}

		assertNull("Cannot create more than " + Constants.MAX_LISTS_PER_USER + " lists per user", faveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}

	/**
	 * When a FaveList is first created, it should not have any FaveItems stored
	 * 
	 * @throws Exception
	 */
	@Test
	public void createdFaveListMustBeEmpty() throws Exception {
		String faveListName = "booya";
		faveListDao.addFaveListForCurrentUser(faveListName);

		FaveList faveList = faveListDao.findFaveList(loggedInUser.getUsername(), faveListName);
		assertEquals("A newly created FaveList must not have any FaveItems stored", 0, faveList.getList().size());
	}

	/**
	 * When a FaveList is created, the name of the list is also stored in the User object. We
	 * denormalize this for better performance so as not to have to perform N FaveList fetches
	 * to retrieve a list of FaveList names for a user.
	 */
	@Test
	public void faveListNameAlsoStoredInUser() throws Exception {
		String firstfaveListName = "gret";
		faveListDao.addFaveListForCurrentUser(firstfaveListName);

		String secondFaveListName = "thasdf";
		faveListDao.addFaveListForCurrentUser(secondFaveListName);

		String msg = "User object must store FaveList name";
		assertEquals(msg, 2, loggedInUser.getHashtags().size());
		assertEquals(msg, loggedInUser.getHashtags().get(0), firstfaveListName);
		assertEquals(msg, loggedInUser.getHashtags().get(1), secondFaveListName);
	}
}
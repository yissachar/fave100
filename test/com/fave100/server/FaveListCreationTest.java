package com.fave100.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.api.AppUserApi;
import com.fave100.server.api.FaveListApi;
import com.fave100.server.api.SongApi;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.shared.Constants;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyFilter;
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
	private FaveListApi faveListApi;
	private HttpServletRequest req;

	@Before
	public void setUp() throws BadRequestException {
		helper.setUp();
		// Create a user
		String username = "tester";
		AppUserDao appUserDao = new AppUserDao();

		AppUserApi appUserApi = new AppUserApi(appUserDao);

		req = TestHelper.newReq();

		loggedInUser = appUserApi.createAppUser(req, username, "goodtests", "testuser@example.com").getAppUser();

		AppUserApi mockAppUserApi = mock(AppUserApi.class);
		when(mockAppUserApi.getLoggedInAppUser(req)).thenReturn(loggedInUser);

		faveListDao = new FaveListDao();
		faveListApi = new FaveListApi(faveListDao, mockAppUserApi, new SongApi());
	}

	@After
	public void tearDown() {
		ObjectifyFilter.complete();
		helper.tearDown();
		loggedInUser = null;
		faveListDao = null;
	}

	@Test
	public void faveListCreated() throws Exception {
		// Create a favelist
		String faveListName = "anewfavelist";
		faveListApi.addFaveListForCurrentUser(req, faveListName);

		// Favelist now exists in datastore
		assertNotNull("Created faveList must exist in datastore", faveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}

	@Test
	public void faveListNameMustBeUniquePerUser() throws BadRequestException, UnauthorizedException, ForbiddenException {
		// Create a favelist
		String faveListName = "boo";
		faveListApi.addFaveListForCurrentUser(req, faveListName);

		// Attempt to add a second favelist with same name
		try {
			faveListApi.addFaveListForCurrentUser(req, faveListName);
			fail("Exception not thrown");
		}
		catch (BadRequestException | UnauthorizedException | ForbiddenException e) {
			// Success!
		}
	}

	@Test
	public void faveListNameMustNotBeNull() {
		// Create a favelist with null name
		String faveListName = null;

		try {
			faveListApi.addFaveListForCurrentUser(req, faveListName);
			fail("Exception not thrown");
		}
		catch (Exception e) {
			// Success!
		}
	}

	@Test
	public void faveListNameMustNotBeEmpty() {
		// Create a favelist with empty name
		String faveListName = "";

		try {
			faveListApi.addFaveListForCurrentUser(req, faveListName);
			fail("Exception not thrown");
		}
		catch (BadRequestException | UnauthorizedException | ForbiddenException e) {
			// Success!
		}

		assertNull("FaveList with empty name must not be created", faveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}

	@Test
	public void faveListNameMustNotBeTooLong() {
		// Create a favelist with a name that is too long
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Constants.MAX_HASHTAG_LENGTH + 1; i++) {
			sb.append("a");
		}

		String faveListName = sb.toString();

		try {
			faveListApi.addFaveListForCurrentUser(req, faveListName);
			fail("Exception not thrown");
		}
		catch (BadRequestException | UnauthorizedException | ForbiddenException e) {
			// Success!
		}

		assertNull("FaveList with a name that is too long must not be created", faveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}

	@Test
	public void faveListNameMustOnlyConsistOfLettersAndNumbers() {
		// Create favelists with special chars and spaces
		String name1 = "foo%";
		String name2 = "^bar";
		String name3 = "baz:";
		String name4 = "baz inga";

		try {
			faveListApi.addFaveListForCurrentUser(req, name1);
			faveListApi.addFaveListForCurrentUser(req, name2);
			faveListApi.addFaveListForCurrentUser(req, name3);
			faveListApi.addFaveListForCurrentUser(req, name4);
			fail("Exception not thrown");
		}
		catch (BadRequestException | UnauthorizedException | ForbiddenException e) {
			// Success!
		}

		String msg = "FaveList with a name that is too long must not be created";
		assertNull(msg, faveListDao.findFaveList(loggedInUser.getUsername(), name1));
		assertNull(msg, faveListDao.findFaveList(loggedInUser.getUsername(), name2));
		assertNull(msg, faveListDao.findFaveList(loggedInUser.getUsername(), name3));
		assertNull(msg, faveListDao.findFaveList(loggedInUser.getUsername(), name4));
	}

	@Test
	public void mustNotCreateTooManyFaveLists() {
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
			faveListApi.addFaveListForCurrentUser(req, faveListName);
			fail("Exception not thrown");
		}
		catch (BadRequestException | UnauthorizedException | ForbiddenException e) {
			// Success!
		}

		assertNull("Cannot create more than " + Constants.MAX_LISTS_PER_USER + " lists per user", faveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}

	/**
	 * When a FaveList is first created, it should not have any FaveItems stored
	 * 
	 * @throws ForbiddenException
	 * @throws UnauthorizedException
	 * @throws BadRequestException
	 * 
	 * @throws Exception
	 */
	@Test
	public void createdFaveListMustBeEmpty() throws BadRequestException, UnauthorizedException, ForbiddenException {
		String faveListName = "booya";
		faveListApi.addFaveListForCurrentUser(req, faveListName);

		FaveList faveList = faveListDao.findFaveList(loggedInUser.getUsername(), faveListName);
		assertEquals("A newly created FaveList must not have any FaveItems stored", 0, faveList.getList().size());
	}

	/**
	 * When a FaveList is created, the name of the list is also stored in the User object. We
	 * denormalize this for better performance so as not to have to perform N FaveList fetches
	 * to retrieve a list of FaveList names for a user.
	 * 
	 * @throws ForbiddenException
	 * @throws UnauthorizedException
	 * @throws BadRequestException
	 */
	@Test
	public void faveListNameAlsoStoredInUser() throws BadRequestException, UnauthorizedException, ForbiddenException {
		String firstfaveListName = "gret";
		faveListApi.addFaveListForCurrentUser(req, firstfaveListName);

		String secondFaveListName = "thasdf";
		faveListApi.addFaveListForCurrentUser(req, secondFaveListName);

		String msg = "User object must store FaveList name";
		assertEquals(msg, 2, loggedInUser.getHashtags().size());
		assertEquals(msg, loggedInUser.getHashtags().get(0), firstfaveListName);
		assertEquals(msg, loggedInUser.getHashtags().get(1), secondFaveListName);
	}
}
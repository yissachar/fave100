package com.fave100.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fave100.server.api.AuthApi;
import com.fave100.server.api.UserApi;
import com.fave100.server.domain.UserRegistration;
import com.fave100.server.domain.appuser.AppUser;
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

@RunWith(PowerMockRunner.class)
@PrepareForTest(UserApi.class)
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
	private HttpServletRequest req;

	@Before
	public void setUp() throws BadRequestException {
		helper.setUp();

		PowerMockito.spy(UserApi.class);

		req = TestHelper.newReq();

		UserRegistration registration = new UserRegistration();
		registration.setUsername("tester");
		registration.setPassword("goodtests");
		registration.setEmail("testuser@example.com");

		loggedInUser = AuthApi.createAppUser(req, registration);
		PowerMockito.stub(PowerMockito.method(UserApi.class, TestHelper.GET_LOGGED_IN_USER_METHOD_NAME)).toReturn(loggedInUser);
	}

	@After
	public void tearDown() {
		ObjectifyFilter.complete();
		helper.tearDown();
		loggedInUser = null;
	}

	@Test
	public void should_create_favelist() throws Exception {
		// Create a favelist
		String faveListName = "anewfavelist";
		UserApi.addFaveListForCurrentUser(req, faveListName);

		// Favelist now exists in datastore
		assertNotNull("Created faveList must exist in datastore", FaveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}

	@Test
	public void should_not_create_favelist_with_duplicate_name_per_user() {
		// Create a favelist
		String faveListName = "boo";
		UserApi.addFaveListForCurrentUser(req, faveListName);

		// Attempt to add a second favelist with same name
		try {
			UserApi.addFaveListForCurrentUser(req, faveListName);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void should_not_create_favelist_with_null_name() {
		// Create a favelist with null name
		String faveListName = null;

		try {
			UserApi.addFaveListForCurrentUser(req, faveListName);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void should_not_create_favelist_with_empty_name() {
		// Create a favelist with empty name
		String faveListName = "";

		try {
			UserApi.addFaveListForCurrentUser(req, faveListName);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void should_not_create_favelist_with_name_that_is_too_long() {
		// Create a favelist with a name that is too long
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Constants.MAX_HASHTAG_LENGTH + 1; i++) {
			sb.append("a");
		}

		String faveListName = sb.toString();

		try {
			UserApi.addFaveListForCurrentUser(req, faveListName);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void should_not_create_favelist_with_non_alphanumeric_name() {
		// Create favelists with special chars and spaces
		String name1 = "foo%";
		String name2 = "^bar";
		String name3 = "baz:";
		String name4 = "baz inga";

		try {
			UserApi.addFaveListForCurrentUser(req, name1);
			UserApi.addFaveListForCurrentUser(req, name2);
			UserApi.addFaveListForCurrentUser(req, name3);
			UserApi.addFaveListForCurrentUser(req, name4);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
		}
	}

	@Test
	public void should_not_create_favelist_if_list_limit_reached() {
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
			UserApi.addFaveListForCurrentUser(req, faveListName);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertEquals(e.getResponse().getStatus(), Response.Status.FORBIDDEN.getStatusCode());
		}

		assertNull("Cannot create more than " + Constants.MAX_LISTS_PER_USER + " lists per user", FaveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
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
	public void should_create_favelist_that_is_empty() {
		String faveListName = "booya";
		UserApi.addFaveListForCurrentUser(req, faveListName);

		FaveList faveList = FaveListDao.findFaveList(loggedInUser.getUsername(), faveListName);
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
	public void should_store_favelist_name_in_user_profile() {
		String firstfaveListName = "gret";
		UserApi.addFaveListForCurrentUser(req, firstfaveListName);

		String secondFaveListName = "thasdf";
		UserApi.addFaveListForCurrentUser(req, secondFaveListName);

		String msg = "User object must store FaveList name";
		assertEquals(msg, 2, loggedInUser.getHashtags().size());
		assertEquals(msg, loggedInUser.getHashtags().get(0), firstfaveListName);
		assertEquals(msg, loggedInUser.getHashtags().get(1), secondFaveListName);
	}
}
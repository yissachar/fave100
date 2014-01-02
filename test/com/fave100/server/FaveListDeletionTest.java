package com.fave100.server;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.fave100.shared.exceptions.user.UsernameAlreadyExistsException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;

public class FaveListDeletionTest {

	static {
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(FaveList.class);
		ObjectifyService.register(EmailID.class);
		ObjectifyService.register(Hashtag.class);
		ObjectifyService.register(Whyline.class);
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
		ObjectifyFilter.complete();
		helper.tearDown();
		loggedInUser = null;
		faveListDao = null;
	}

	@Test
	public void faveListDeleted() throws Exception {
		// Create a favelist
		String faveListName = "favelisttodelete";
		faveListDao.addFaveListForCurrentUser(faveListName);

		// Delete it
		faveListDao.deleteFaveListForCurrentUser(faveListName);

		// Favelist no longer exists in datastore
		assertNull("Deleted FaveList must no longer exist in datastore", faveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}
}
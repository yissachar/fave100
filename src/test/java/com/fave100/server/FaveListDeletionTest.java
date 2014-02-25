package com.fave100.server;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.api.AppUserApi;
import com.fave100.server.api.FaveListApi;
import com.fave100.server.api.SongApi;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.server.domain.favelist.Hashtag;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
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
	public void faveListDeleted() throws BadRequestException, UnauthorizedException, ForbiddenException {
		// Create a favelist
		String faveListName = "favelisttodelete";
		faveListApi.addFaveListForCurrentUser(req, faveListName);

		// Delete it
		faveListApi.deleteFaveListForCurrentUser(req, faveListName);

		// Favelist no longer exists in datastore
		assertNull("Deleted FaveList must no longer exist in datastore", faveListDao.findFaveList(loggedInUser.getUsername(), faveListName));
	}
}
package com.fave100.server;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.fave100.server.api.ApiTest;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;

public class DatastoreLeakStateTest extends ApiTest {

	// run this test twice to prove we're not leaking any state across tests
	private void doTest() {
		String username = "test";
		assertNull(AppUserDao.findAppUser(username));
		final AppUser appUser = new AppUser(username);
		ofy().save().entity(appUser).now();
		assertNotNull(AppUserDao.findAppUser(username));

	}

	@Test
	public void testInsert1() {
		doTest();
	}

	@Test
	public void testInsert2() {
		doTest();
	}

}

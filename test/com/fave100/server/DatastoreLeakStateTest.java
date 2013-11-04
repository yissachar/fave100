package com.fave100.server;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.domain.appuser.AppUser;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.dev.HighRepJobPolicy;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;

public class DatastoreLeakStateTest {

	static {
		ObjectifyService.register(AppUser.class);
	}

	private static final class CustomHighRepJobPolicy implements HighRepJobPolicy {
		static int count = 0;

		@Override
		public boolean shouldApplyNewJob(final Key entityGroup) {
			// every other new job fails to apply
			return count++ % 2 == 0;
		}

		@Override
		public boolean shouldRollForwardExistingJob(final Key entityGroup) {
			// every other exsting job fails to apply
			return count++ % 2 == 0;
		}
	}

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setAlternateHighRepJobPolicyClass(CustomHighRepJobPolicy.class));

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	// run this test twice to prove we're not leaking any state across tests
	private void doTest() {
		assertEquals(0, ofy().load().type(AppUser.class).count());
		final AppUser appUser = new AppUser("test");
		ofy().save().entity(appUser).now();
		final AppUser appUser2 = new AppUser("test2");
		ofy().save().entity(appUser2).now();
		// Only sees the first save
		assertEquals(1, ofy().load().type(AppUser.class).count());

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

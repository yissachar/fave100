package test.com.fave100.server;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.fave100.shared.exceptions.user.UsernameAlreadyExistsException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;

public class UniqueDatastoreTest {

	static {
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(FaveList.class);
		ObjectifyService.register(EmailID.class);
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
	public void uniqueNameTest() {
		// TODO: Fails right now because of dependency on RequestFactoryServlet
		assertEquals(0, ofy().load().type(AppUser.class).count());
		final AppUser appUser = new AppUser("test");
		try {
			AppUser.createAppUser("test", "123456", "test@example.com");
		}
		catch (UsernameAlreadyExistsException | EmailIDAlreadyExistsException e) {
			e.printStackTrace();
		}
		// Make sure first user was saved
		assertEquals(appUser, AppUser.findAppUser("test"));
		// Make sure user with duplicate name not saved
		try {
			AppUser.createAppUser("test", "123456", "test2@example.com");
		}
		catch (UsernameAlreadyExistsException | EmailIDAlreadyExistsException e) {
			e.printStackTrace();
		}
	}
}
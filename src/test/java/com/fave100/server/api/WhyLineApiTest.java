package com.fave100.server.api;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.WhylineCollection;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.Hashtag;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;

public class WhyLineApiTest {

	static {
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(FaveList.class);
		ObjectifyService.register(EmailID.class);
		ObjectifyService.register(Hashtag.class);
		ObjectifyService.register(Whyline.class);
	}

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		ObjectifyFilter.complete();
		helper.tearDown();
	}

	@Test
	public void should_not_find_whylines_if_they_do_not_exist() {
		WhylineCollection whylineCollection = SongApi.getWhylines("BbK4Ex");
		assertEquals(whylineCollection.getItems().size(), 0);
	}

}
